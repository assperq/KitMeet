package com.example.profile.presentation

import androidx.compose.runtime.Composable

@Composable
actual fun EditProfileScreen(
    userId: String,
    onSave: (String, String, String, String, String?, List<String>?, String, String) -> Unit
) {
}