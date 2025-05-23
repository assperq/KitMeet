package org.digital.kitmeet.notifications

expect class FCMTokenProvider {
    fun initialize()
    fun getCurrentToken(): String?

    companion object {
        fun getInstance(): FCMTokenProvider
    }
}