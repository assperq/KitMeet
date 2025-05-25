package com.digital.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.registration.data.UserRemoteDatasourceImpl
import com.digital.registration.data.UserRepositoryImpl
import com.digital.registration.domain.UserRepository
import com.digital.settings.data.SettingsRepositoryImpl
import com.digital.settings.domain.Settings
import com.digital.settings.domain.SettingsRepository
import com.digital.settings.domain.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val settings : MutableStateFlow<Settings> = MutableStateFlow(Settings(false, Theme.System, "", ""))

    init {
       getSettings()
    }

    fun singOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }

    fun setTheme(theme : Theme) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.setTheme(theme)
        }
        settings.value = settings.value.copy(theme = theme)
    }

    fun setEnablePush(enable : Boolean) {
        viewModelScope.launch {
            repository.setEnablePush(enable)
        }
        settings.value = settings.value.copy(enablePush = enable)
    }

    fun setEmail(email : String) {
        viewModelScope.launch {
            repository.setEmail(email)
        }
        settings.value = settings.value.copy(email = email)
    }

    fun setPassword(password : String) {
        viewModelScope.launch {
            repository.setPassword(password)
        }
        settings.value = settings.value.copy(password = password)
    }



    private fun getSettings() {
        viewModelScope.launch {
            val theme = repository.getTheme()
            val enablePush = repository.getEnablePush()
            val email = repository.getEmail()
            val password = repository.getPassword()

            settings.emit(Settings(
                enablePush,
                theme,
                email,
                password
            ))
        }
    }
}

fun provideSettingsViewModel() : SettingsViewModel {
    return SettingsViewModel(SettingsRepositoryImpl(), UserRepositoryImpl(UserRemoteDatasourceImpl()))
}