package com.digital.chat.domain

interface FCMTokenRegistrar {
    fun registerToken(token: String)
}