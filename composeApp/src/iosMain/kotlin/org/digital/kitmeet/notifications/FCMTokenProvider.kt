package org.digital.kitmeet.notifications

// iosMain/kotlin/com/example/core/notifications/IosFCMTokenProvider.kt
actual class FCMTokenProvider {
    private var currentToken: String? = null

    // iOS будет устанавливать этот callback через Swift
    private var tokenUpdateCallback: ((String) -> Unit)? = null

    fun setTokenUpdateCallback(callback: (String) -> Unit) {
        this.tokenUpdateCallback = callback
    }

    actual fun initialize() {
        // Инициализация происходит из Swift кода
    }

    fun onTokenReceived(token: String) {
        currentToken = token
        tokenUpdateCallback?.invoke(token)
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