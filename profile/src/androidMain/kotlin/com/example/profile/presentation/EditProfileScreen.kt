package com.example.profile.presentation

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.profile.uploadImageToSupabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun EditProfileScreen(
    userId: String,
    onSave: (String, String, String, String, String?, List<String>?, String, String) -> Unit
) {
    val accentColor = Color(0xFF7F265B)
    val context = LocalContext.current

    var step by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var name by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var mainPhoto by remember { mutableStateOf("") }
    var lookingFor by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    val galleryPhotos = remember { mutableStateListOf<String>() }
    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }

    val steps = listOf("Основное", "Образование", "Дополнительно")
    val lookingForOptions = listOf("Ищу разработчиков", "Ищу друзей", "Ищу киско-жён")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val path = it.toString()
            if (step == 0) {
                mainPhoto = path
            } else if (step == 2) {
                if (selectedPhotoIndex == -1) {
                    if (galleryPhotos.size < 5) {
                        galleryPhotos.add(path)
                    }
                } else {
                    selectedPhotoIndex?.let { index -> galleryPhotos[index] = path }
                }
            }
        }
    }

    suspend fun uploadAndSaveProfile() {
        val uploadedMainPhoto = mainPhoto.takeIf { it.isNotBlank() }?.let {
            uploadImageToSupabase(context, userId, Uri.parse(it), "main_photo_${System.currentTimeMillis()}.jpg")
        }

        val uploadedGalleryPhotos = galleryPhotos.mapIndexedNotNull { index, path ->
            uploadImageToSupabase(
                context,
                userId,
                Uri.parse(path),
                "gallery_${System.currentTimeMillis()}_$index.jpg"
            )
        }

        onSave(
            userId,
            name,
            profession,
            group,
            uploadedMainPhoto,
            uploadedGalleryPhotos,
            lookingFor,
            aboutMe
        )
    }

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(96.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            steps.forEachIndexed { index, title ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (index <= step) accentColor else Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Color.White, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        title,
                        fontSize = 11.sp,
                        color = if (index == step) accentColor else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (step) {
                    0 -> {
                        Text("Шаг 1: Основное", color = accentColor, style = MaterialTheme.typography.titleLarge)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Имя") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Главное фото", style = MaterialTheme.typography.labelLarge)

                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable {
                                    selectedPhotoIndex = null
                                    launcher.launch("image/*")
                                },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, accentColor)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (mainPhoto.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(mainPhoto),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = "Добавить", tint = accentColor)
                                }
                            }
                        }
                    }

                    1 -> {
                        Text("Шаг 2: Образование", color = accentColor, style = MaterialTheme.typography.titleLarge)

                        OutlinedTextField(
                            value = profession,
                            onValueChange = { profession = it },
                            label = { Text("Профессия") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = group,
                            onValueChange = { group = it },
                            label = { Text("Группа") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = lookingFor,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Кого ищет") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                lookingForOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            lookingFor = option
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        Text("Шаг 3: Дополнительно", color = accentColor, style = MaterialTheme.typography.titleLarge)

                        OutlinedTextField(
                            value = aboutMe,
                            onValueChange = { aboutMe = it },
                            label = { Text("Обо мне") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Дополнительные фото", style = MaterialTheme.typography.labelLarge)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            itemsIndexed(galleryPhotos) { index, photo ->
                                Card(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clickable {
                                            selectedPhotoIndex = index
                                            launcher.launch("image/*")
                                        },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(photo),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            if (galleryPhotos.size < 5) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable {
                                                selectedPhotoIndex = -1
                                                launcher.launch("image/*")
                                            },
                                        border = BorderStroke(1.dp, accentColor),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = accentColor)
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (name.isBlank() || profession.isBlank() || group.isBlank()
                                    || lookingFor.isBlank() || aboutMe.isBlank()
                                ) {
                                    errorMessage = "Пожалуйста, заполните все обязательные поля."
                                } else {
                                    errorMessage = null
                                    coroutineScope.launch {
                                        uploadAndSaveProfile()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Сохранить", color = Color.White)
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Шаги навигации
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step-- },
                            border = BorderStroke(1.dp, accentColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
                        ) {
                            Text("Назад")
                        }
                    }
                    if (step < steps.lastIndex) {
                        Button(
                            onClick = { step++ },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Далее", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
