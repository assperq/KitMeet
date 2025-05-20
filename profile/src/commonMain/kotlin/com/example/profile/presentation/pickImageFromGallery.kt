package com.example.profile.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun pickImageFromGallery(
    onImageSelected: (String) -> Unit
): () -> Unit