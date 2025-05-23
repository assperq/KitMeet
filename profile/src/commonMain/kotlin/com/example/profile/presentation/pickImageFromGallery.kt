package com.example.profile.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun pickImageFromGallery(
    userId: String,
    onImageUploaded: (String?) -> Unit,
    oldFilePath: String? // без значения по умолчанию
): () -> Unit