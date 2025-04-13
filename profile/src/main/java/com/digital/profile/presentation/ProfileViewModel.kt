package com.digital.profile.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.profile.data.model.Profile
import com.digital.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    val state = mutableStateOf<ProfileState>(ProfileState.Loading)

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            state.value = try {
                val profile = repository.getProfile("current_user_id")
                ProfileState.Success(profile)
            } catch (e: Exception) {
                ProfileState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: Profile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}