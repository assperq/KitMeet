package com.example.profile.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.uploadImageToSupabase
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

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
                    it,
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

