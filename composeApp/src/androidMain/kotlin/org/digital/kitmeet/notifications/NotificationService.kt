package org.digital.kitmeet.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import org.digital.kitmeet.R
import java.lang.ref.WeakReference
import kotlin.random.Random

actual class NotificationService(
    var context : WeakReference<Context>
) {

    actual fun showNotification(
        title: String,
        message: String,
        channelId: String,
        channelName: String
    ) {
        val notificationManager = context.get()!!.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        createNotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

        val notification = NotificationCompat.Builder(context.get()!!, channelId)
            .setSmallIcon(R.drawable.logo_kitmeet)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }

    actual fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        ).apply {
            description = "Channel description"
        }

        val notificationManager = context.get()!!.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    actual companion object {
        private var instance : NotificationService? = null

        actual fun getInstance() : NotificationService {
            return instance ?: throw IllegalStateException(
                "FCMTokenProvider not initialized. Call initialize() first."
            )
        }

        fun init(context: Context) {
            instance = NotificationService(WeakReference(context))
        }
    }
}
