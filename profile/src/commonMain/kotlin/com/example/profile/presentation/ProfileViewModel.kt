package com.example.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import com.example.profile.data.Profile
import com.example.profile.data.ProfileRepository
import com.example.profile.data.ProfileRepositoryImpl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


class ProfileViewModel(
    private val supabaseClient: SupabaseClient
) : ViewModel() {
    private val _isProfileCompleteFlow = MutableStateFlow(false)
    val isProfileCompleteFlow: StateFlow<Boolean> = _isProfileCompleteFlow.asStateFlow()
    private val _currentProfile = MutableStateFlow<Profile?>(null)
    val currentProfile: StateFlow<Profile?> = _currentProfile.asStateFlow()

    suspend fun saveProfile(userId: String, name: String, profession: String, group: String): Boolean {
        return try {
            // Теперь передаем user_id в объект Profile
            val profile = Profile(userId, name, profession, group)

            // Запрос для обновления профиля через Supabase
            val response = supabaseClient
                .from("profiles")
                .upsert(profile)  // Выполняем upsert

            // Печатаем ответ для отладки
            println("📦 Ответ от Supabase: $response")

            // Если данные успешно обновлены
            _currentProfile.value = profile
            _isProfileCompleteFlow.value = true
            true

        } catch (e: Exception) {
            // Обработка исключений
            println("❌ Exception при сохранении профиля: ${e.message}")
            _isProfileCompleteFlow.value = false
            false
        }
    }

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            try {
                val profile = supabaseClient
                    .from("profiles")
                    .select {
                        filter {
                            "user_id = '$userId'"
                        }
                    }
                    .decodeSingle<Profile>()

                _currentProfile.value = profile
                _isProfileCompleteFlow.value = true
                println("📥 Профиль загружен: $profile")
            } catch (e: Exception) {
                println("⚠️ Не удалось загрузить профиль: ${e.message}")
                _currentProfile.value = null
                _isProfileCompleteFlow.value = false
            }
        }
    }
}

class ProfileViewModelFactory(
    private val supabaseClient: SupabaseClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return ProfileViewModel(supabaseClient) as T
    }
}