package org.digital.kitmeet.notifications

import android.content.Context
import com.digital.chat.domain.FCMTokenRegistrar
import com.google.firebase.messaging.FirebaseMessaging
import org.digital.kitmeet.log
import java.lang.ref.WeakReference


actual class FCMTokenProvider {
    private val firebaseMessaging = FirebaseMessaging.getInstance()
    private var currentToken: String? = null


    private val tokenRegistrar: FCMTokenRegistrar by lazy {
        ServiceLocator.tokenRegistrar
    }

    actual fun initialize() {
        updateToken()
    }

    private fun updateToken() {
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentToken = task.result
                currentToken?.let { token ->
                    tokenRegistrar.registerToken(token)
                }
            }
        }
    }

    actual fun getCurrentToken(): String? = currentToken

    actual companion object {
        private var instance: FCMTokenProvider? = null

        actual fun getInstance(): FCMTokenProvider {
            return instance ?: throw IllegalStateException(
                "FCMTokenProvider not initialized. Call initialize() first."
            )
        }

        fun initialize() {
            if (instance == null) {
                instance = FCMTokenProvider()
            }
        }
    }
}