package com.digital.chat.presentation.ui.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digital.chat.data.FindRepositoryImpl
import com.digital.chat.domain.FindRepository
import com.example.profile.data.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindViewModel(
    private val findRepository: FindRepository = FindRepositoryImpl()
) : ViewModel() {

    val isSearching = MutableStateFlow(false)

    private val _users : MutableStateFlow<List<Profile>> = MutableStateFlow(emptyList())
    val users = _users.asStateFlow()

    fun findUsers(query : String, currentUser : String) {
        viewModelScope.launch {
            isSearching.value = true
            _users.value = findRepository.findUsers(query, currentUser)
            isSearching.value = false
        }
    }
}