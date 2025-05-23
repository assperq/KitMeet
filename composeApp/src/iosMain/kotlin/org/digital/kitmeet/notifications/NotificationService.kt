package org.digital.kitmeet.notifications

import com.benasher44.uuid.uuid4
import okio.Lock.Companion.instance
import platform.UIKit.*
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.freeze


actual class NotificationService {
    actual fun showNotification(
        title: String,
        message: String,
        channelId: String,
        channelName: String
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound())
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = uuid4().toString(),
            content = content,
            trigger = null
        )

        center.addNotificationRequest(request) { error ->
            error?.let { println("Notification error: $it") }
        }
    }

    actual fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {}

    actual companion object {
        actual fun getInstance() : NotificationService {
            return NotificationService()
        }
    }
}