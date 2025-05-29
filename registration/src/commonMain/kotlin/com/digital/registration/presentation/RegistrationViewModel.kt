package com.digital.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.registration.data.UserRemoteDatasourceImpl
import com.digital.registration.data.UserRepositoryImpl
import com.digital.registration.domain.UserRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val state: MutableStateFlow<State> = MutableStateFlow(State.StartState)

    sealed class State {
        class Error(val e: Throwable) : State()
        object StartState : State()
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.singIn(email, password)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess() // Навигация на главном потоке
                    }
                    state.value = State.StartState
                }
                .onFailure {
                    state.value = State.Error(it)
                }
        }
    }

    fun singUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.singUp(email, password)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                    state.value = State.StartState
                }
                .onFailure {
                    state.value = State.Error(it)
                }
        }
    }
}

fun provideRegistrationViewModel() : RegistrationViewModel {
    return RegistrationViewModel(UserRepositoryImpl(UserRemoteDatasourceImpl()))
}