package com.digital.chat.domain

import com.example.profile.data.Profile
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName

@Serializable
data class ConversationDtoModel(
    @SerialName("conversation_id") val id: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("user1_id") val user1: String,
    @SerialName("user2_id") val user2: String,
    var lastMessage: Message? = null
)

@Serializable
data class Conversation(
    @SerialName("conversation_id") val id: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("user1_profile") val user1: Profile,
    @SerialName("user2_profile") val user2: Profile,
    var lastMessage: Message? = null
)

@Serializable
data class Message(
    val id: String,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("is_read") var isRead : Boolean,
    val showDate: Boolean = false
)

@Serializable
data class FCMToken(
    @SerialName("user_id") val userId: String,
    @SerialName("token") val token: String,
    @SerialName("updated_at") val updatedAt : Instant
)