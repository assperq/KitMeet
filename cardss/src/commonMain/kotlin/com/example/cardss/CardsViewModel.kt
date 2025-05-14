package com.example.cardss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profile.data.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles = _profiles.asStateFlow()

    init {
        loadProfiles()
    }

    fun loadProfiles(gender: String? = null, course: Int? = null, specialization: String? = null) {
        viewModelScope.launch {
            try {
                val response = supabaseClient
                    .from("profiles")
                    .select()
                    .decodeList<Profile>()

                // Логирование полученных данных
                println("Полученные профили: $response")

                _profiles.value = response
            } catch (e: Exception) {
                println("Ошибка загрузки профилей: ${e.message}")
            }
        }
    }

    fun removeProfile(profile: Profile) {
        _profiles.value = _profiles.value.filterNot { it.user_id == profile.user_id }
    }
}