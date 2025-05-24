package com.example.profile.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.profile.data.ProfileRepositoryImpl
import com.example.profile.presentation.ProfileViewModel
import io.github.jan.supabase.SupabaseClient
import kotlin.reflect.KClass

class ProfileViewModelFactory(
    private val supabaseClient: SupabaseClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val repository = ProfileRepositoryImpl(supabaseClient)
        return ProfileViewModel(repository) as T
    }
}