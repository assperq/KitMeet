package com.digital.settings.domain

interface SettingsRepository {
    suspend fun setTheme(theme: Theme)
    suspend fun getTheme() : Theme

    suspend fun setEnablePush(enable : Boolean)
    suspend fun getEnablePush() : Boolean
}