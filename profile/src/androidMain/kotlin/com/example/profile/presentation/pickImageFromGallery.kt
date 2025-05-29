package com.example.profile.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.profile.presentation.editProfileScreens.uploadImageToSupabase
import kotlinx.coroutines.launch

@Composable
actual fun pickImageFromGallery(
    userId: String,
    onImageUploaded: (String?) -> Unit,
    oldFilePath: String?,
    isMainPhoto: Boolean
): () -> Unit {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val fileName = if (isMainPhoto)
                    "main_photo.jpg"
                else
                    "gallery_photo_${System.currentTimeMillis()}.jpg"

                val uploadedUrl = uploadImageToSupabase(
                    context,
                    userId,
                    it.toString(),
                    fileName,
                    if (isMainPhoto) "$userId/$fileName" else null
                )

                onImageUploaded(uploadedUrl)
            }
        }
    }

    return {
        launcher.launch("image/*")
    }
}

