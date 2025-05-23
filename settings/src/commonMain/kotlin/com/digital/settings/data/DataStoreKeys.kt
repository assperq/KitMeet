package com.digital.settings.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val THEME_KEY = stringPreferencesKey("theme")
    val ENABLE_PUSH_KEY = booleanPreferencesKey("enable_push")
    val EMAIL_KEY = stringPreferencesKey("email")
    val PASSWORD_KEY = stringPreferencesKey("password")
}