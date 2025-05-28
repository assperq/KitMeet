package com.digital.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.UUID
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid
import com.benasher44.uuid.uuid4
import com.digital.chat.data.ChatRepositoryImpl
import com.digital.chat.domain.ChatRepository
import com.digital.chat.domain.Conversation
import com.digital.chat.domain.FCMMessage
import com.digital.chat.domain.FCMTokenRegistrar
import com.digital.chat.domain.Message
import com.digital.chat.domain.PresenceState
import com.digital.supabaseclients.SupabaseManager
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeJoinsAs
import io.github.jan.supabase.realtime.decodeLeavesAs
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.toJsonObject
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpHeaders.Date
import io.ktor.http.headers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

class ChatViewModel(
    private val repository: ChatRepository = ChatRepositoryImpl()
) : ViewModel(), FCMTokenRegistrar {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    val connectedUsers = mutableSetOf<PresenceState>()

    private val _currentConversation = MutableStateFlow<Conversation?>(null)
    val currentConversation = _currentConversation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var messagesSubscription: Job? = null

    val currentUserId = MutableStateFlow("")

    // Добавленный метод для создания чата при матче
    fun createConversationIfNeeded(user1Id: String, user2Id: String) {
        viewModelScope.launch {
            try {
                // Проверяем существование чата
                val existing = repository.findConversation(user1Id, user2Id)
                if (existing == null) {
                    // Создаем новый чат
                    repository.createConversation(user1Id, user2Id)

                    // Получаем созданный чат
                    val newConversation = repository.findConversation(user1Id, user2Id)

                    // Отправляем системное сообщение
                    newConversation?.let {
                        sendSystemMessage(
                            conversationId = it.id,
                            text = "Вы понравились друг другу! Начните общение"
                        )
                    }

                    // Обновляем список бесед
                    loadConversations()
                }
            } catch (e: Exception) {
                println("Ошибка при создании чата: ${e.message}")
            }
        }
    }

    // Метод для отправки системных сообщений
    private suspend fun sendSystemMessage(conversationId: String, text: String) {
        val message = Message(
            id = uuid4().toString(),
            conversationId = conversationId,
            senderId = "system",
            content = text,
            createdAt = Clock.System.now(),
            isRead = false
        )
        repository.sendMessage(message)
    }

    fun createConversation(user1Id : String, user2Id : String) {
        viewModelScope.launch {
            repository.createConversation(user1Id, user2Id)
            loadConversations()
        }
    }

    fun findConversation(user1Id : String, user2Id : String) {
        try {
            viewModelScope.launch {
                val conversation = repository.findConversation(user1Id, user2Id)
                println(conversation)
                if (conversation != null) {
                    selectConversation(conversation)
                }
            }
        } catch (e : Throwable) {
            println(e.message)
        }
    }

    fun loadConversations() {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: ""
        currentUserId.value = userId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _conversations.value = repository.getConversations(userId)
                println(_conversations.value)
            } catch (e: Exception) {
                println(e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun markAsRead(messageId : String) {
        repository.markAsRead(messageId)
        val list : List<Message> = _messages.value
        list.filter { it.id == messageId }.forEach { it.isRead = true }
        _messages.emit(list)
    }

    fun changeLastMessage(message : Message) {
        _conversations.value = _conversations.value.map { conversation ->
            if (conversation.id == message.conversationId) {
                conversation.copy(lastMessage = message)
            } else {
                conversation
            }
        }
    }

    fun selectConversation(conversation: Conversation) {
        _currentConversation.value = conversation
        subscribeToNewMessages(conversation.id)
        loadMessages(conversation.id)

        // Безопасно проверяем наличие последнего сообщения
        conversation.lastMessage?.let {
            changeLastMessage(it.copy(isRead = true))
        }

        viewModelScope.launch {
            val unreadMessages = _messages.value.filter {
                !it.isRead && it.senderId != supabaseClient.auth.currentUserOrNull()?.id
            }
            unreadMessages.forEach { message ->
                markAsRead(message.id)
            }
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _messages.value = repository.getMessages(conversationId).sortedBy { it.createdAt }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subscribeToNewMessages(conversationId: String) {
        messagesSubscription?.cancel()
        messagesSubscription = viewModelScope.launch {
            val channel = supabaseClient.channel("messages_$conversationId")
            channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public").onEach {
                val message = parseInsertMessage(it.record)
                if (message.conversationId == _currentConversation.value?.id) {
                    val list = _messages.value + message
                    _messages.emit(list)

                    if (message.senderId != supabaseClient.auth.currentUserOrNull()?.id) {
                        markAsRead(message.id)
                    }
                }

                changeLastMessage(message)
            }.launchIn(viewModelScope)

            channel.subscribe(true)
            val myState = buildJsonObject {
                put("user_id", Json.encodeToJsonElement(currentUserId.value))
            }
            channel.track(myState)

            channel.presenceChangeFlow().collect {
                connectedUsers.addAll(it.decodeJoinsAs<PresenceState>())
                connectedUsers.removeAll(it.decodeLeavesAs<PresenceState>())
            }
        }
    }


    fun sendMessage(content: String) {
        val conversationId = _currentConversation.value?.id ?: return
        val message = Message(
            id = uuid4().toString(),
            conversationId = conversationId,
            senderId = currentUserId.value,
            content = content,
            createdAt = System.now(),
            isRead = false
        )

        viewModelScope.launch {
            repository.sendMessage(message)
            changeLastMessage(message)
        }

        try {
            if (connectedUsers.size <= 1) {
                viewModelScope.launch {
                    val (otherUserLocal, currentUser) = when (currentUserId.value) {
                        _currentConversation.value!!.user1.user_id -> _currentConversation.value!!.user2 to _currentConversation.value!!.user1
                        else -> _currentConversation.value!!.user1 to _currentConversation.value!!.user2
                    }
                    val fcmMessage = FCMMessage(
                        message,
                        currentUser
                    )
                    supabaseClient.functions.invoke(
                        function = "send-message",
                        body = buildJsonObject {
                            put("message", Json.encodeToJsonElement(fcmMessage))
                            put("conversation_id", Json.encodeToJsonElement(conversationId))
                            put(
                                "fcmToken", Json.encodeToJsonElement(
                                    repository.getFCMToken(otherUserLocal.user_id).token
                                )
                            )
                        }
                    )
                }
            }
        }
        catch (ex : Throwable) {
            println(ex.message)
        }
    }

    fun deleteConversation() {
        viewModelScope.launch {
            repository.deleteConversation(currentConversation.value!!.id)
            loadConversations()
            _currentConversation.value = null
        }
    }

    fun unsubscribeToChannel(conversationId : String) {
        try {
            viewModelScope.launch {
                val channel = supabaseClient.channel("messages_$conversationId")
                channel.unsubscribe()
            }
        }
        catch (_ : Throwable) { }
    }

    override fun registerToken(token: String) {
        viewModelScope.launch {
            repository.registerFCMToken(supabaseClient.auth.currentUserOrNull()?.id ?: "", token)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesSubscription?.cancel()
    }
}

private fun parseInsertMessage(record: JsonObject): Message {
    val json = Json { ignoreUnknownKeys = true }
    val recordJson = json.encodeToJsonElement(record)
    return json.decodeFromJsonElement(recordJson)
}