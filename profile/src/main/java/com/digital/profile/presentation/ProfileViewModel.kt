package com.digital.profile.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.profile.domain.model.Profile
import com.digital.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {
    private val _profileState = mutableStateOf<Profile?>(null)
    val profileState: State<Profile?> = _profileState

    private val _uiState = mutableStateOf(ProfileUiState())
    val uiState: State<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase(1).let { profile ->
                _profileState.value = profile
                _uiState.value = _uiState.value.copy(
                    photos = profile.photos,
                    mainPhoto = profile.mainPhoto
                )
            }
        }
    }

    // Обработчики UI событий
    fun toggleAboutMeExpansion() {
        _uiState.value = _uiState.value.copy(
            isAboutMeExpanded = !_uiState.value.isAboutMeExpanded
        )
    }

    fun handleImageSelection(imageRes: String) {
        _uiState.value = _uiState.value.copy(
            selectedImage = imageRes
        )
    }
}

data class ProfileUiState(
    val isAboutMeExpanded: Boolean = false,
    val selectedImage: String? = null,
    val photos: List<String> = emptyList(),
    val mainPhoto: String = ""
)