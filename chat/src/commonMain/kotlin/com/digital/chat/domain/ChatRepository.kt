package com.digital.chat.domain

interface ChatRepository {
    suspend fun createConversation(user1Id : String, user2Id : String)
    suspend fun findConversation(user1Id : String, user2Id : String) : Conversation?
    suspend fun getConversations(userId: String): List<Conversation>
    suspend fun getMessages(conversationId: String): List<Message>
    suspend fun markAsRead(messageId: String)
    suspend fun sendMessage(message: Message)
    suspend fun registerFCMToken(userId: String, token: String)
    suspend fun getFCMToken(userId: String) : FCMToken
    suspend fun deleteConversation(conversationId: String)
}