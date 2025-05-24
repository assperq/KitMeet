package com.digital.chat.data

import com.benasher44.uuid.uuid4
import com.digital.chat.domain.ChatRepository
import com.digital.chat.domain.Conversation
import com.digital.chat.domain.ConversationDtoModel
import com.digital.chat.domain.FCMToken
import com.digital.chat.domain.Message
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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
        postgrest.from("user_fcm_tokens")
            .upsert(FCMToken(userId, token))
    }
}