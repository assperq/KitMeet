package com.example.profile.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.registration.domain.UserRepository
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.data.UserProfile
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    fun getOrThrow(): T {
        return when(this) {
            is Success -> data
            is Error -> throw exception
        }
    }
}
class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _profileState = mutableStateOf<UserProfile?>(null)
    val profileState: State<UserProfile?> get() = _profileState

    private val _loadingState = mutableStateOf(false)
    val loadingState: State<Boolean> get() = _loadingState

    private val _errorState = mutableStateOf<String?>(null)
    val errorState: State<String?> get() = _errorState

    private fun getCurrentUserId(): String {
        val currentUser = SupabaseManager.supabaseClient.auth.currentUserOrNull()
        return currentUser?.id ?: throw IllegalStateException("User not authenticated")
    }

    fun loadProfile() {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                val userId = getCurrentUserId()
                when(val result = userRepository.getProfile(userId)) {
                    is Result.Success -> {
                        val data = result.data as? UserProfile
                        if (data != null) {
                            _profileState.value = data
                        } else {
                            _errorState.value = "Invalid profile data format"
                        }
                    }
                    is Result.Error -> _errorState.value = result.exception.message
                }
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun saveProfile(
        name: String,
        age: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                val userId = getCurrentUserId()
                val profile = UserProfile(
                    userId = userId,
                    name = name,
                    age = age
                )

                when(val result = userRepository.saveProfile(profile)) {
                    is Result.Success -> onSuccess()
                    is Result.Error -> _errorState.value = result.exception.message
                }
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _loadingState.value = false
            }
        }
    }
}
