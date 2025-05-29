package com.example.profile.presentation.editProfileScreens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

// androidMain/kotlin/com/example/image/AndroidImagePicker.kt
@Composable
actual fun rememberImagePicker(): ImagePicker {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    return remember {
        object : ImagePicker {
            @Composable
            override fun registerPicker(
                onImagePicked: (String?) -> Unit,
                isMainPhoto: Boolean
            ): () -> Unit {
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
                                context = context,
                                userId = "userId_placeholder", // Замените на актуальный userId
                                imagePath = it.toString(),
                                fileName = fileName
                            )
                            onImagePicked(uploadedUrl)
                        }
                    } ?: onImagePicked(null)
                }

                return { launcher.launch("image/*") }
            }
        }
    }
}

@Composable
private fun ImagePickerHandler(
    launcher: ActivityResultLauncher<String>,
    context: Context,
    userId: String,
    onImagePicked: (String?) -> Unit,
    oldFilePath: String?,
    isMainPhoto: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(launcher) {
        launcher.launch("image/*")
    }

    LaunchedEffect(Unit) {
        // Обработка результата
        launcher.unwrap().addCallback { uri ->
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
                        oldFilePath
                    )
                    onImagePicked(uploadedUrl)
                }
            }
        }
    }
}

// Вспомогательное расширение для доступа к callback'ам
private fun ActivityResultLauncher<String>.unwrap(): ActivityResultLauncher<String> {
    return this
}

private fun ActivityResultLauncher<String>.addCallback(callback: (Uri?) -> Unit) {
    // Реализация callback'а
}

@Composable
actual fun AsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    coil.compose.AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

actual suspend fun uploadImageToSupabase(
    context: Any,
    userId: String,
    imagePath: String,
    fileName: String,
    oldFilePath: String?
): String? {
    val androidContext = context as Context
    val uri = Uri.parse(imagePath)

    val inputStream = androidContext.contentResolver.openInputStream(uri) ?: return null
    val byteArray = inputStream.readBytes()

    val path = oldFilePath ?: "$userId/$fileName"

    SupabaseManager.supabaseClient.storage
        .from("profile-photos")
        .upload(
            path = path,
            data = byteArray,
            options = { upsert = true }
        )

    return SupabaseManager.supabaseClient.storage
        .from("profile-photos")
        .publicUrl(path) + "?t=${System.currentTimeMillis()}"
}