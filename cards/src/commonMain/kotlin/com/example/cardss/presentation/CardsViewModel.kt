package com.example.cardss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardss.domain.CardsRepositoryImpl
import com.example.cardss.presentation.SwipeTracker
import com.example.profile.data.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: CardsRepositoryImpl,
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Состояние для отслеживания матчей
    private val _matchFound = MutableStateFlow<Profile?>(null)
    val matchFound: StateFlow<Profile?> = _matchFound.asStateFlow()

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
                val likes = repository.getAllLikes()

                // Лайки, которые поставил текущий пользователь
                val likedByMe = likes.filter {
                    it.from_user_id == currentUserId && it.status == "accepted"
                }

                val acceptedIds = likedByMe.map { it.to_user_id }

                val allProfiles = repository.getAllProfiles()
                val acceptedProfiles = allProfiles.filter { it.user_id in acceptedIds }

                _acceptedProfiles.value = acceptedProfiles

                // ✅ Отдельно проверяем на взаимные лайки (матчи)
                for (profile in acceptedProfiles) {
                    val reverseLike = repository.getLike(profile.user_id, currentUserId)
                    if (reverseLike?.status == "accepted") {
                        _matchFound.value = profile
                        break // Показываем только один матч за раз
                    }
                }
            } catch (e: Exception) {
                println("❌ Ошибка загрузки принятых аккаунтов: ${e.message}")
                _acceptedProfiles.value = emptyList()
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

                // Проверка на взаимный лайк (матч)
                checkForMatch(profile.user_id, currentUserId)

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

    // Проверка на взаимный лайк
    private suspend fun checkForMatch(otherUserId: String, currentUserId: String) {
        // Проверяем, есть ли лайк от другого пользователя к текущему
        val reverseLike = repository.getLike(otherUserId, currentUserId)

        // Если есть взаимный лайк
        if (reverseLike?.status == "accepted") {
            // Получаем профиль пользователя, с которым совпали
            val matchedProfile = repository.getProfileById(otherUserId)
            matchedProfile?.let {
                _matchFound.value = it
            }
        }
    }

    // Сброс состояния матча
    fun clearMatch() {
        _matchFound.value = null
    }
}