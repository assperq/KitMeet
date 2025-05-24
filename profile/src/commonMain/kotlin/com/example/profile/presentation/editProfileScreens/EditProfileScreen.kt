package com.example.profile.presentation.editProfileScreens

import androidx.compose.runtime.Composable

@Composable
expect fun EditProfileScreen(
    userId: String,
    onSave: (
        id: String,
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
    ) -> Unit
)




