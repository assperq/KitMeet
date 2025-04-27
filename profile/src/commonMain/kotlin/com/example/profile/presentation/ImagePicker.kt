package com.example.profile.presentation

import io.github.jan.supabase.SupabaseClient

interface ImagePicker {
    fun pickImage(onImagePicked: (String) -> Unit)
}