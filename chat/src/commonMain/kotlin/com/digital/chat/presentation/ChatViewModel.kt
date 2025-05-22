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
import com.digital.chat.domain.Message
import com.digital.supabaseclients.SupabaseManager
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.ktor.http.HttpHeaders.Date
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

class ChatViewModel(
    private val repository: ChatRepository = ChatRepositoryImpl(),
) : ViewModel() {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _currentConversation = MutableStateFlow<Conversation?>(null)
    val currentConversation = _currentConversation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var messagesSubscription: Job? = null

    val currentUserId = MutableStateFlow("")

    fun createConversation(user1Id : String, user2Id : String) {
        viewModelScope.launch {
            repository.createConversation(user1Id, user2Id)
            loadConversations()
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: ""
                currentUserId.value = userId
                println(currentUserId.value)
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
            channel.subscribe()
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
            try {
                repository.sendMessage(message)
                supabaseClient.functions.invoke("send-message", mapOf(
                        "message" to content,
                        "conversation_id" to conversationId,
                        "sender_id" to currentUserId.value
                    ))
            } catch (e: Exception) { }
        }

        viewModelScope.launch {
            changeLastMessage(message)
        }
    }

    fun registerFCMToken(token: String) {
        viewModelScope.launch {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch
            repository.registerFCMToken(userId, token)
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
