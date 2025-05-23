package com.digital.chat.data

import co.touchlab.kermit.Logger.Companion.e
import com.benasher44.uuid.uuid4
import com.digital.chat.domain.ChatRepository
import com.digital.chat.domain.Conversation
import com.digital.chat.domain.ConversationDtoModel
import com.digital.chat.domain.FCMToken
import com.digital.chat.domain.Message
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import com.example.profile.data.Profile
import com.example.profile.presentation.ProfileScreen
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.toJsonObject
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

class ChatRepositoryImpl : ChatRepository {
    private val postgrest = supabaseClient.postgrest


    override suspend fun createConversation(user1Id : String, user2Id : String) {
        postgrest.from("conversation").insert(ConversationDtoModel(
            id = uuid4().toString(),
            createdAt = Clock.System.now(),
            user1 = user1Id,
            user2 = user2Id,
            lastMessage = null
        ))
    }

    override suspend fun getConversations(userId: String): List<Conversation> {
        return try {
            val response = postgrest.rpc(
                function = "get_chats",
                parameters = JsonObject(mapOf("select_user_id" to JsonPrimitive(userId)))
            ).decodeList<Conversation>()
            response.map {
                Conversation(
                    id = it.id,
                    user1 = it.user1,
                    user2 = it.user2,
                    createdAt = it.createdAt,
                    lastMessage = getLastMessage(it.id)
                )
            }
        } catch (ex : Throwable) {
            println(ex.message)
            emptyList<Conversation>()
        }
    }

    suspend fun getLastMessage(conversationId: String): Message {
        return postgrest.from("messages")
            .select {
                filter {
                    eq("conversation_id", conversationId)
                }
                order("created_at", Order.DESCENDING)
                limit(1)
            }.decodeSingle<Message>()
    }

    override suspend fun markAsRead(messageId: String) {
        postgrest.from("messages")
            .update({
                set("is_read", true)
            }) {
                filter {
                    eq("id", messageId)
                }
            }
    }

    override suspend fun getMessages(conversationId: String): List<Message> {
        return postgrest.from("messages")
            .select {
                filter {
                    eq("conversation_id", conversationId)
                }
                order("created_at", order = Order.DESCENDING)
                limit(100)
            }.decodeList<Message>()
    }

    override suspend fun sendMessage(message: Message) {
        postgrest.from("messages")
            .insert(message)
    }

    override suspend fun registerFCMToken(userId: String, token: String) {
        try {
            postgrest.from("user_fcm_tokens")
                .upsert(FCMToken(userId, token, Clock.System.now()))
        } catch (_ : Exception) {}
    }

    override suspend fun getFCMToken(userId: String): FCMToken {
        return postgrest.from("user_fcm_tokens").select() {
            filter {
                eq("user_id", userId)
            }
        }.decodeSingle<FCMToken>()
    }
}