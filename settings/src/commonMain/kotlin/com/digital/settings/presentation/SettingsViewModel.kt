package com.digital.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.settings.data.SettingsRepositoryImpl
import com.digital.settings.domain.Settings
import com.digital.settings.domain.SettingsRepository
import com.digital.settings.domain.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings : MutableStateFlow<Settings> = MutableStateFlow(Settings(false, Theme.System))

    init {
       getSettings()
    }

    fun setTheme(theme : Theme) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.setTheme(theme)
        }
        settings.value = Settings(
            theme = theme,
            enablePush = settings.value.enablePush
        )
    }

    fun setEnablePush(enable : Boolean) {
        viewModelScope.launch {
            repository.setEnablePush(enable)
        }
        settings.value = Settings(
            theme = settings.value.theme,
            enablePush = enable
        )
    }

    private fun getSettings() {
        viewModelScope.launch {
            val theme = repository.getTheme()
            val enablePush = repository.getEnablePush()

            settings.emit(Settings(
                enablePush,
                theme
            ))
        }
    }
}

fun provideSettingsViewModel() : SettingsViewModel {
    return SettingsViewModel(SettingsRepositoryImpl())
}