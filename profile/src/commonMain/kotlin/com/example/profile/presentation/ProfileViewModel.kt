package com.example.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.profile.data.Profile
import com.example.profile.domain.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

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
