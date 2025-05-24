package com.digital.chat.data

import co.touchlab.kermit.Logger.Companion.e
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

class ChatRepositoryImpl : ChatRepository {
    private val postgrest = supabaseClient.postgrest


    override suspend fun createConversation(user1Id : String, user2Id : String) {
        postgrest.rpc("ensure_conversation", parameters = buildJsonObject {
            put("p_user_a", Json.encodeToJsonElement(user1Id))
            put("p_user_b", Json.encodeToJsonElement(user2Id))
        })
    }

    override suspend fun getConversations(userId: String): List<Conversation> {
        return postgrest.rpc(
            function = "get_chats",
            parameters = buildJsonObject {
                put("select_user_id", Json.encodeToJsonElement(userId))
            }
        ).decodeList<Conversation>()
    }

    suspend fun getLastMessage(conversationId: String): Message? {
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
        } catch (ex : Exception) {
            println(ex.message)
        }
    }

    override suspend fun getFCMToken(userId: String): FCMToken {
        return postgrest.from("user_fcm_tokens").select() {
            filter {
                eq("user_id", userId)
            }
        }.decodeSingle<FCMToken>()
    }

    override suspend fun deleteConversation(conversationId: String) {
        postgrest.from("conversations").delete() {
            filter {
                eq("id", conversationId)
            }
        }
    }
}