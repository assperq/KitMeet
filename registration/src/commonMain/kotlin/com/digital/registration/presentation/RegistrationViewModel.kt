package com.digital.registration.presentation

import androidx.lifecycle.ViewModel
import com.digital.registration.data.UserRemoteDatasourceImpl
import com.digital.registration.data.UserRepositoryImpl
import com.digital.registration.domain.UserRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val state : MutableStateFlow<State> = MutableStateFlow(State.StartState)
    sealed class State
    {
        data object Success : State()
        data class Error(val e: Throwable) : State()
        data object StartState : State()
    }

    fun singIn(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = userRepository.singIn(email, password)
            emitRes(res)
        }
    }

    fun singUp(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = userRepository.singUp(email, password)
            emitRes(res)
        }
    }

    private suspend fun emitRes(res: Result<Unit>) {
        if (res.isFailure) {
            state.emit(State.Error(res.exceptionOrNull()!!))
        }
        else {
            state.emit(State.Success)
        }
    }
}

fun provideRegistrationViewModel() : RegistrationViewModel {
    return RegistrationViewModel(UserRepositoryImpl(UserRemoteDatasourceImpl()))
}