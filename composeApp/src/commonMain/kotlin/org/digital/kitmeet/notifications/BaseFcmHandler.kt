package org.digital.kitmeet.notifications

import org.digital.kitmeet.log

class BaseFcmHandler(
    private val notificationService: NotificationService
) : FcmHandler {
    private val tokenListeners = mutableListOf<(String) -> Unit>()

    override fun onNewToken(token: String) {
        tokenListeners.forEach { it(token) }
    }

    override fun onMessageReceived(data: Map<String, String>) {
        val title = data["title"] ?: "New message"
        val body = data["message"] ?: ""
        log("Received FCM message: $data")
        notificationService.showNotification(
            title = title,
            message = body,
            channelId = "1",
            channelName = "CHECK"
        )
    }

    fun addTokenListener(listener: (String) -> Unit) {
        tokenListeners.add(listener)
    }
}