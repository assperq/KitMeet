package com.example.cardss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profile.data.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
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
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id

                val response = supabaseClient
                    .from("profiles")
                    .select()
                    .decodeList<Profile>()

                println("Полученные профили: $response")

                // Фильтрация — убираем текущего пользователя из списка
                _profiles.value = response.filter { profile ->
                    val groupStartsWithCourse = course == null || profile.group.firstOrNull()?.digitToIntOrNull() == course
                    profile.user_id != currentUserId &&
                            (gender == null || gender == "Оба" || profile.gender == gender) &&
                            groupStartsWithCourse &&
                            (specialization == null || specialization == "Любая" || profile.specialty == specialization)
                }

            } catch (e: Exception) {
                println("Ошибка загрузки профилей: ${e.message}")
            }
        }
    }

    fun removeProfile(profile: Profile) {
        _profiles.value = _profiles.value.filterNot { it.user_id == profile.user_id }
    }
}