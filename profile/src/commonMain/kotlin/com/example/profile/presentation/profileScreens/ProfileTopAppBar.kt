package com.example.profile.presentation.profileScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import com.example.profile.data.Profile
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.pickImageFromGallery
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ProfileTopAppBar(
    profile: Profile,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    isEditMode: Boolean,
    viewModel: ProfileViewModel,
    onEditToggle: () -> Unit
) {
    val oldPath = extractStoragePath(profile.main_photo)

    val launchImagePicker = pickImageFromGallery(
        userId = profile.user_id,
        oldFilePath = oldPath,
        onImageUploaded = { newUrl ->
            newUrl?.let {
                viewModel.updateMainPhoto(it)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .zIndex(0f)
    ) {
        KamelImage(
            resource = { asyncPainterResource(profile.main_photo) },
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
    ) {
        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = Color(0xFFD2D2D2).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Назад",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(30.dp)
                )
            }
        } else {
            IconButton(
                onClick = onEditToggle, // Используем колбэк из параметров
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = Color(0xFFD2D2D2).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditMode) "Готово" else "Изменить",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        if (isEditMode) {
            IconButton(
                onClick = launchImagePicker,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .background(Color(0xFFD2D2D2).copy(alpha = 0.8f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Сменить фото",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (!showBackButton) {
            IconButton(
                onClick = { /* Настройки */ },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color(0xFFD2D2D2).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

fun extractStoragePath(publicUrl: String): String? {
    val prefix = "https://kmehxgdlljbtrfnlzbgr.supabase.co/storage/v1/object/public/"
    return if (publicUrl.startsWith(prefix)) {
        publicUrl.removePrefix(prefix)
    } else null
}