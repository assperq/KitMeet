package com.digital.settings.data

import androidx.datastore.preferences.core.edit
import com.digital.settings.domain.SettingsRepository
import com.digital.settings.domain.Theme
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl : SettingsRepository {

    private val dataStore = DataStoreManager.dataStore

    override suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.THEME_KEY] = theme.name
        }
    }

    override suspend fun getTheme(): Theme {
        return Theme.valueOf(dataStore.data.first()[DataStoreKeys.THEME_KEY] ?: Theme.System.name)
    }

    override suspend fun setEnablePush(enable: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.ENABLE_PUSH_KEY] = enable
        }
    }

    override suspend fun getEnablePush(): Boolean {
        return dataStore.data.first()[DataStoreKeys.ENABLE_PUSH_KEY] == true
    }

    override suspend fun setEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.EMAIL_KEY] = email
        }
    }

    override suspend fun getEmail(): String {
        return dataStore.data.first()[DataStoreKeys.EMAIL_KEY] ?: ""
    }

    override suspend fun setPassword(password: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.PASSWORD_KEY] = password
        }
    }

    override suspend fun getPassword(): String {
        return dataStore.data.first()[DataStoreKeys.PASSWORD_KEY] ?: ""
    }

}