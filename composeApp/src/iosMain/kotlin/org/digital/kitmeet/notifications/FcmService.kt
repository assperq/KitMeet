@file:OptIn(ExperimentalForeignApi::class)

package org.digital.kitmeet.notifications

import platform.darwin.NSObject
import cocoapods.FirebaseMessaging.*
import kotlinx.cinterop.ExperimentalForeignApi

class IosFcmDelegate : NSObject(), FIRMessagingDelegateProtocol  {
    override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
        val token = didReceiveRegistrationToken ?: return
        token.let { token ->
            FcmDelegate.handler?.onNewToken(token)
        }
    }
}

fun setupFcmDelegate() {
    FIRMessaging.messaging().delegate = IosFcmDelegate()
}