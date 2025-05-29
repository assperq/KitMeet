package org.digital.kitmeet

import android.app.Application
import com.google.firebase.FirebaseApp
import org.digital.kitmeet.di.KoinInitializer
import org.digital.kitmeet.di.initKoin
import org.digital.kitmeet.notifications.FCMTokenProvider
import org.digital.kitmeet.notifications.NotificationService

class KitMeetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        NotificationService.init(applicationContext)
        FCMTokenProvider.initialize()
        KoinInitializer(this).init()
    }
}