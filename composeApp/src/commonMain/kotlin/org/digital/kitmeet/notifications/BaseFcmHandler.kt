package org.digital.kitmeet.notifications

import com.digital.chat.domain.FCMMessage
import com.digital.chat.domain.Message
import com.digital.chat.presentation.ChatViewModel
import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json

class BaseFcmHandler(
    private val notificationService: NotificationService,
    private val chatViewModel: ChatViewModel
) : FcmHandler {
    private val tokenListeners = mutableListOf<(String) -> Unit>()

    override fun onNewToken(token: String) {
        tokenListeners.forEach { it(token) }
    }

    override fun onMessageReceived(data: Map<String, String>) {
        val body = data["message"] ?: ""
        val message = Json.decodeFromString<FCMMessage>(body)
        chatViewModel.changeLastMessage(message.message)
        notificationService.showNotification(
            title = message.sender.name,
            message = message.message.content,
            channelId = "1",
            channelName = "CHECK"
        )
    }

    fun addTokenListener(listener: (String) -> Unit) {
        tokenListeners.add(listener)
    }
}