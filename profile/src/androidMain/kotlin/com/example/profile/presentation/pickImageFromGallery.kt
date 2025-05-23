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
    oldFilePath: String? // добавить параметр с путем старого фото (опционально)
): () -> Unit {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            Log.d("ImagePicker", "URI selected: $it")

            coroutineScope.launch {
                // 1. Удаляем старое фото, если есть
                oldFilePath?.let { path ->
                    try {
                        SupabaseManager.supabaseClient.storage
                            .from("profile-photos")
                        Log.d("ImagePicker", "Old photo removed: $path")
                    } catch (e: Exception) {
                        Log.e("ImagePicker", "Failed to remove old photo: $e")
                    }
                }

                // 2. Загружаем новое фото
                val fileName = "profile_photo_${System.currentTimeMillis()}.jpg"
                val uploadedUrl = uploadImageToSupabase(context, userId, it, fileName)

                // 3. Колбек с новым URL
                onImageUploaded(uploadedUrl)
            }
        }
    }

    return {
        Log.d("ImagePicker", "Launching gallery...")
        launcher.launch("image/*")
    }
}