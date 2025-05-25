package com.digital.settings.domain

interface SettingsRepository {
    suspend fun setTheme(theme: Theme)
    suspend fun getTheme() : Theme

    suspend fun setEnablePush(enable : Boolean)
    suspend fun getEnablePush() : Boolean

    suspend fun setEmail(email : String)
    suspend fun getEmail() : String

    suspend fun setPassword(password : String)
    suspend fun getPassword() : String
}