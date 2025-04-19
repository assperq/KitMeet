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

}