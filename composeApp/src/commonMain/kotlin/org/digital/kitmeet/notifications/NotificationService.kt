package org.digital.kitmeet.notifications

expect class NotificationService {
    fun showNotification(
        title: String,
        message: String,
        channelId: String,
        channelName: String
    )

    fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    )

    companion object {
        fun getInstance() : NotificationService
    }
}