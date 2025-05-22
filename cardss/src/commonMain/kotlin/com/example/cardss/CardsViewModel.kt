package com.example.cardss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val supabaseClient: SupabaseClient,
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
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                // Загружаем все лайки текущего пользователя (accepted и rejected)
                val allLikes = supabaseClient
                    .from("likes")
                    .select()
                    .decodeList<LikeEntry>() ?: emptyList()

                // Исключаем профили, которые уже лайкнуты (accepted или rejected)
                val excludedUserIds = allLikes
                    .filter { it.from_user_id == currentUserId && (it.status == "accepted" || it.status == "rejected") }
                    .map { it.to_user_id }

                // Загружаем все профили
                val allProfiles = supabaseClient
                    .from("profiles")
                    .select()
                    .decodeList<Profile>() ?: emptyList()

                val filtered = allProfiles.filter { profile ->
                    val groupStartsWithCourse = course == null || profile.group.firstOrNull()?.digitToIntOrNull() == course
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
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                val likedUsers = supabaseClient
                    .from("likes")
                    .select()
                    .decodeList<LikeEntry>()
                    .filter { it.from_user_id == currentUserId && it.status == "accepted" }

                val acceptedIds = likedUsers.map { it.to_user_id }

                if (acceptedIds.isNotEmpty()) {
                    val allProfiles = supabaseClient
                        .from("profiles")
                        .select()
                        .decodeList<Profile>() ?: emptyList()

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
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                val rejectedLikes = supabaseClient
                    .from("likes")
                    .select()
                    .decodeList<LikeEntry>()
                    .filter { it.from_user_id == currentUserId && it.status == "rejected" }

                val rejectedIds = rejectedLikes.map { it.to_user_id }

                if (rejectedIds.isNotEmpty()) {
                    val allProfiles = supabaseClient
                        .from("profiles")
                        .select()
                        .decodeList<Profile>() ?: emptyList()

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

    // При свайпе вправо (принять)
    fun acceptProfile(profile: Profile) {
        viewModelScope.launch {
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                // Добавляем или обновляем лайк со статусом accepted
                supabaseClient.from("likes").upsert(
                    mapOf(
                        "from_user_id" to currentUserId,
                        "to_user_id" to profile.user_id,
                        "status" to "accepted"
                    )
                )

                // Удаляем из текущих профилей (колоды)
                _profiles.value = _profiles.value.filter { it.user_id != profile.user_id }

                val updatedCount = swipeTracker.incrementSwipeCount()
                _cardsSwipedToday.value = updatedCount

                // Перезагружаем accepted
                loadAcceptedProfiles()

            } catch (e: Exception) {
                println("❌ Ошибка при принятии пользователя: ${e.message}")
            }
        }
    }

    // При свайпе влево (отклонить)
    fun rejectProfile(profile: Profile) {
        viewModelScope.launch {
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                // Добавляем или обновляем лайк со статусом rejected
                supabaseClient.from("likes").upsert(
                    mapOf(
                        "from_user_id" to currentUserId,
                        "to_user_id" to profile.user_id,
                        "status" to "rejected"
                    )
                )

                // Удаляем из текущих профилей (колоды)
                _profiles.value = _profiles.value.filter { it.user_id != profile.user_id }

                val updatedCount = swipeTracker.incrementSwipeCount()
                _cardsSwipedToday.value = updatedCount

                // Перезагружаем rejected
                loadRejectedProfiles()

            } catch (e: Exception) {
                println("❌ Ошибка при отклонении пользователя: ${e.message}")
            }
        }
    }

    // Удаляем профиль из accepted или rejected (возвращаем в колоду)
    fun removeProfileFromList(profile: Profile, listType: String) {
        viewModelScope.launch {
            try {
                val currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch

                // Удаляем лайк из базы
                supabaseClient.from("likes").delete {
                    filter {
                        eq("from_user_id", currentUserId)
                        eq("to_user_id", profile.user_id)
                        eq("status", listType) // "accepted" или "rejected"
                    }
                }

                // Обновляем соответствующий список
                when (listType) {
                    "accepted" -> loadAcceptedProfiles()
                    "rejected" -> loadRejectedProfiles()
                }

                // Перезагружаем колоду (профили)
                loadProfiles()

            } catch (e: Exception) {
                println("❌ Ошибка при удалении профиля из списка $listType: ${e.message}")
            }
        }
    }
}

@Serializable
data class LikeEntry(
    val from_user_id: String,
    val to_user_id: String,
    val status: String
)

class SwipeTracker(private val settings: Settings) {

    companion object {
        private const val KEY_COUNT = "swipe_count"
        private const val KEY_DATE = "swipe_date"
    }

    private fun today(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun getSwipeCount(): Int {
        val savedDateStr = settings.getStringOrNull(KEY_DATE)
        val savedDate = savedDateStr?.let { LocalDate.parse(it) }
        val currentDate = today()

        return if (savedDate == currentDate) {
            settings.getInt(KEY_COUNT, 0)
        } else {
            0
        }
    }

    fun incrementSwipeCount(): Int {
        val currentDate = today()
        val savedDateStr = settings.getStringOrNull(KEY_DATE)
        val savedDate = savedDateStr?.let { LocalDate.parse(it) }

        return if (savedDate == currentDate) {
            val newCount = settings.getInt(KEY_COUNT, 0) + 1
            settings.putInt(KEY_COUNT, newCount)
            newCount
        } else {
            settings.putString(KEY_DATE, currentDate.toString())
            settings.putInt(KEY_COUNT, 1)
            1
        }
    }

    fun reset() {
        settings.putInt(KEY_COUNT, 0)
        settings.putString(KEY_DATE, today().toString())
    }
}

