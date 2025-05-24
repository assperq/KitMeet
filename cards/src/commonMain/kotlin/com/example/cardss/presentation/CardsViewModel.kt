package com.example.cardss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardss.data.CardsRepository
import com.example.cardss.presentation.SwipeTracker
import com.example.profile.data.Profile
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

class CardsViewModel(
    private val repository: CardsRepository,
    private val swipeTracker: SwipeTracker
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles = _profiles.asStateFlow()

    private val _acceptedProfiles = MutableStateFlow<List<Profile>>(emptyList())
    val acceptedProfiles = _acceptedProfiles.asStateFlow()

    private val _rejectedProfiles = MutableStateFlow<List<Profile>>(emptyList())
    val rejectedProfiles = _rejectedProfiles.asStateFlow()

    private val _cardsSwipedToday = MutableStateFlow(0)
    val cardsSwipedToday: StateFlow<Int> = _cardsSwipedToday

    init {
        _cardsSwipedToday.value = swipeTracker.getSwipeCount()
        loadProfiles()
        loadAcceptedProfiles()
        loadRejectedProfiles()
    }

    fun loadProfiles(gender: String? = null, course: Int? = null, specialization: String? = null) {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                val allLikes = repository.getAllLikes()
                val excludedUserIds = allLikes
                    .filter { it.from_user_id == currentUserId && (it.status == "accepted" || it.status == "rejected") }
                    .map { it.to_user_id }

                val allProfiles = repository.getAllProfiles()
                val filtered = allProfiles.filter { profile ->
                    val groupStartsWithCourse = course == null || profile.group?.firstOrNull()?.digitToIntOrNull() == course
                    profile.user_id != currentUserId &&
                            (gender == null || gender == "Оба" || profile.gender == gender) &&
                            groupStartsWithCourse &&
                            (specialization == null || specialization == "Любая" || profile.specialty == specialization) &&
                            profile.user_id !in excludedUserIds
                }
                _profiles.value = filtered

            } catch (e: Exception) {
                println("Ошибка загрузки профилей: ${e.message}")
            }
        }
    }

    fun loadAcceptedProfiles() {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                val likedUsers = repository.getAllLikes()
                    .filter { it.from_user_id == currentUserId && it.status == "accepted" }
                val acceptedIds = likedUsers.map { it.to_user_id }

                if (acceptedIds.isNotEmpty()) {
                    val allProfiles = repository.getAllProfiles()
                    val acceptedProfiles = allProfiles.filter { it.user_id in acceptedIds }
                    _acceptedProfiles.value = acceptedProfiles
                } else {
                    _acceptedProfiles.value = emptyList()
                }
            } catch (e: Exception) {
                println("❌ Ошибка загрузки принятых аккаунтов: ${e.message}")
            }
        }
    }

    fun loadRejectedProfiles() {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                val rejectedLikes = repository.getAllLikes()
                    .filter { it.from_user_id == currentUserId && it.status == "rejected" }
                val rejectedIds = rejectedLikes.map { it.to_user_id }

                if (rejectedIds.isNotEmpty()) {
                    val allProfiles = repository.getAllProfiles()
                    val rejectedProfiles = allProfiles.filter { it.user_id in rejectedIds }
                    _rejectedProfiles.value = rejectedProfiles
                } else {
                    _rejectedProfiles.value = emptyList()
                }
            } catch (e: Exception) {
                println("❌ Ошибка загрузки отвергнутых аккаунтов: ${e.message}")
            }
        }
    }

    fun acceptProfile(profile: Profile) {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                repository.upsertLike(currentUserId, profile.user_id, "accepted")

                _profiles.value = _profiles.value.filter { it.user_id != profile.user_id }
                val updatedCount = swipeTracker.incrementSwipeCount()
                _cardsSwipedToday.value = updatedCount
                loadAcceptedProfiles()
            } catch (e: Exception) {
                println("❌ Ошибка при принятии пользователя: ${e.message}")
            }
        }
    }

    fun rejectProfile(profile: Profile) {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                repository.upsertLike(currentUserId, profile.user_id, "rejected")

                _profiles.value = _profiles.value.filter { it.user_id != profile.user_id }
                val updatedCount = swipeTracker.incrementSwipeCount()
                _cardsSwipedToday.value = updatedCount
                loadRejectedProfiles()
            } catch (e: Exception) {
                println("❌ Ошибка при отклонении пользователя: ${e.message}")
            }
        }
    }

    fun removeProfileFromList(profile: Profile, listType: String) {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId() ?: return@launch
                repository.deleteLike(currentUserId, profile.user_id, listType)

                when (listType) {
                    "accepted" -> loadAcceptedProfiles()
                    "rejected" -> loadRejectedProfiles()
                }
                loadProfiles()
            } catch (e: Exception) {
                println("❌ Ошибка при удалении профиля из списка $listType: ${e.message}")
            }
        }
    }
}