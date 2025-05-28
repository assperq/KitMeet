package com.digital.settings.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.settings.data.SettingsRepositoryImpl
import com.digital.settings.domain.Settings
import com.digital.settings.domain.SettingsRepository
import com.digital.settings.domain.Theme
import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.locks.SynchronizedObject
import io.ktor.utils.io.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    private var _settings: MutableStateFlow<Settings?> = MutableStateFlow(null)
    val setting = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            _settings.value = getSettings()
        }
    }

    fun singOut() {
        viewModelScope.launch {
            repository.setEmail("")
            repository.setPassword("")
            SupabaseManager.supabaseClient.auth.signOut()
        }
    }

    fun setTheme(theme : Theme) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.setTheme(theme)
        }
        _settings.value = _settings.value?.copy(theme = theme)
    }

    fun setEnablePush(enable : Boolean) {
        viewModelScope.launch {
            repository.setEnablePush(enable)
        }
        _settings.value = _settings.value?.copy(enablePush = enable)
    }

    fun setEmail(email : String) {
        viewModelScope.launch {
            repository.setEmail(email)
        }
        _settings.value = _settings.value?.copy(email = email)
    }

    fun setPassword(password : String) {
        viewModelScope.launch {
            repository.setPassword(password)
        }
        _settings.value = _settings.value?.copy(password = password)
    }

    private suspend fun getSettings() : Settings {
        val theme = repository.getTheme()
        val enablePush = repository.getEnablePush()
        val email = repository.getEmail()
        val password = repository.getPassword()

        return Settings(
            enablePush = enablePush,
            theme = theme,
            email = email,
            password = password
        )
    }

    companion object {
        private var INSTANCE : SettingsViewModel? = null

        @OptIn(InternalAPI::class)
        fun getViewModel() : SettingsViewModel {
            return INSTANCE ?: synchronized(SynchronizedObject()) {
                val inst = provideSettingsViewModel()
                INSTANCE = inst
                inst
            }
        }
    }
}

fun provideSettingsViewModel() : SettingsViewModel {
    return SettingsViewModel(SettingsRepositoryImpl())
}