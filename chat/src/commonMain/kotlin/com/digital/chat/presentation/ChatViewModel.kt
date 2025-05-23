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
import com.digital.chat.domain.FCMTokenRegistrar
import com.digital.chat.domain.Message
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
    private val pushMessageBlock : suspend (
        content : String, conversationId : String, fcmId : String
    ) -> Unit = { _, _, _ -> },
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
    private var channels : MutableList<RealtimeChannel> = mutableListOf()

    val currentUserId = MutableStateFlow("")

    fun createConversation(user1Id : String, user2Id : String) {
        viewModelScope.launch {
            repository.createConversation(user1Id, user2Id)
            loadConversations()
        }
    }

    fun loadConversations() {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: ""
        currentUserId.value = userId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _conversations.value = repository.getConversations(userId)
            } catch (e: Exception) {
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

    private suspend fun changeLastMessage(message : Message) {
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
        loadMessages(conversation.id)
        subscribeToNewMessages(conversation.id)

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
            val presenceChangeFlow = channel.presenceChangeFlow()

            presenceChangeFlow.collect {
                connectedUsers += it.decodeJoinsAs<PresenceState>()
                connectedUsers -= it.decodeLeavesAs<PresenceState>()
            }
            channel.subscribe()
            channels.add(channel)
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
//                if (connectedUsers.size < 2) {
//
//                }
            launch {
                val otherUserLocal = when (currentUserId.value) {
                _currentConversation.value!!.user1.user_id -> _currentConversation.value!!.user2
                else -> _currentConversation.value!!.user1
                }
                supabaseClient.functions.invoke(
                    function = "send-message",
                    body = buildJsonObject {
                        put("message", Json.encodeToJsonElement(content))
                        put("conversation_id", Json.encodeToJsonElement(conversationId))
                        put("fcmToken", Json.encodeToJsonElement(repository.getFCMToken(otherUserLocal.user_id).token))
                    }
                )
            }

            changeLastMessage(message)
        }
    }

    override fun registerToken(token: String) {
        viewModelScope.launch {
            repository.registerFCMToken(currentUserId.value, token)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesSubscription?.cancel()
        channels.forEach {
            viewModelScope.launch {
                it.unsubscribe()
            }
        }
    }
}

private fun parseInsertMessage(record: JsonObject): Message {
    val json = Json { ignoreUnknownKeys = true }
    val recordJson = json.encodeToJsonElement(record)
    return json.decodeFromJsonElement(recordJson)
}

@Serializable
data class PresenceState(val username: String)