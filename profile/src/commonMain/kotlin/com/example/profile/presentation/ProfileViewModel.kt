package com.example.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import com.example.profile.data.Profile
import com.example.profile.domain.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    private val postgrest = supabaseClient.postgrest

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isProfileCompleteFlow = MutableStateFlow(false)
    val isProfileCompleteFlow: StateFlow<Boolean> = _isProfileCompleteFlow.asStateFlow()

    private val _currentProfile = MutableStateFlow<Profile?>(null)
    val currentProfile: StateFlow<Profile?> = _currentProfile.asStateFlow()

    suspend fun saveProfile(
        userId: String,
        name: String,
        profession: String,
        group: String,
        mainPhoto: String,
        galleryPhotos: List<String>,
        lookingFor: String,
        aboutMe: String,
        gender: String,
        age: Int,
        status: String,
        specialty: String
    ): Boolean {
        val profile = Profile(
            user_id = userId,
            name = name,
            profession = profession,
            group = group,
            main_photo = mainPhoto,
            gallery_photos = galleryPhotos,
            looking_for = lookingFor,
            about_me = aboutMe,
            gender = gender,
            age = age,
            status = status,
            specialty = specialty
        )

        val success = repository.saveProfile(profile)
        if (success) {
            _currentProfile.value = profile
            _isProfileCompleteFlow.value = true
        } else {
            _isProfileCompleteFlow.value = false
        }
        return success
    }

    fun updateMainPhoto(uri: String) {
        _currentProfile.value = _currentProfile.value?.copy(main_photo = uri)
    }

    fun addPhotoToGallery(newUrl: String) {
        _currentProfile.value = _currentProfile.value?.let { profile ->
            val updatedGallery = profile.gallery_photos.toMutableList().apply {
                add(newUrl)
            }
            profile.copy(gallery_photos = updatedGallery)
        }
    }

//    suspend fun getRawConversationsByUser(userId: String): List<JsonObject> {
//        val conversationsUser1 = postgrest.from("conversations")
//            .select()
//            .filter("user1_id=eq.$userId") // если filter есть
//
//        val conversationsUser2 = postgrest.from("conversations")
//            .select()
//            .filter("user2_id=eq.$userId")
//
//        val mapById = (conversationsUser1 + conversationsUser2)
//            .associateBy { it["conversation_id"]?.jsonPrimitive?.content ?: "" }
//
//        return mapById.values.toList()
//    }

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = repository.loadProfile(userId)
            _currentProfile.value = profile
            _isProfileCompleteFlow.value = profile != null
            _isLoading.value = false
        }
    }
}
