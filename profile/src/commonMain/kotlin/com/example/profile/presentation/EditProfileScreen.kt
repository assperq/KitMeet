package com.example.profile.presentation

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.FileUploadResponse
import io.github.jan.supabase.storage.storage
import kitmeet.profile.generated.resources.Res
import kitmeet.profile.generated.resources.icon_back
import kitmeet.profile.generated.resources.photo1
import kitmeet.profile.generated.resources.photo2
import kitmeet.profile.generated.resources.photo3
import kitmeet.profile.generated.resources.photo4
import kitmeet.profile.generated.resources.photo5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    onSave: (String, String, String, String, String?, List<String>?, String, String) -> Unit
) {
    val accentColor = Color(0xFF7F265B)

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

    val samplePhotos = listOf(
        "sample1.jpg",
        "sample2.jpg",
        "sample3.jpg"
    )
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showGalleryPhotoDialog by remember { mutableStateOf(false) }

//    suspend fun handleUploadPhoto(resourceName: String) {
//        val uploadedUrl = uploadPhotoToSupabase(resourceName, "profile-photos")
//        if (uploadedUrl != null) {
//            mainPhoto = uploadedUrl
//        } else {
//            // Если не удалось загрузить
//            println("Ошибка загрузки изображения")
//        }
//    }

//    // Пример вызова загрузки для главного фото
//    LaunchedEffect(Unit) {
//        handleUploadPhoto("photo1.jpg")  // Пример, как загрузить фото
//    }

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

        // Центрированная правая колонка
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 500.dp), // выравниваем ширину всех блоков
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

                        if (showPhotoDialog) {
                            ChoosePhotoDialog(
                                onSelect = { selectedPhoto ->
                                    mainPhoto = selectedPhoto
                                },
                                onDismiss = { showPhotoDialog = false }
                            )
                        }

                        Text("Главное фото", style = MaterialTheme.typography.labelLarge)

                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable { showPhotoDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, accentColor)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (mainPhoto.isNotBlank()) {
                                    Image(
                                        painter = painterResource(getDrawableResource(mainPhoto)),
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
                                    modifier = Modifier.size(80.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Image(
                                        painter = painterResource(getDrawableResource(photo)),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                // При клике на фото передаем его индекс в обработчик
                                                showGalleryPhotoDialog = true
                                                // Храним индекс выбранного фото для обновления
                                                selectedPhotoIndex = index
                                            }
                                    )
                                }
                            }

                            if (galleryPhotos.size < 5) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable {
                                                // При клике на плюсик показываем диалог для выбора фото
                                                showGalleryPhotoDialog = true
                                                selectedPhotoIndex = -1 // -1 означает, что это новое фото
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


                        if (showGalleryPhotoDialog) {
                            ChoosePhotoDialog(
                                onSelect = { selectedPhoto ->
                                    // Если selectedPhotoIndex == -1, значит это новое фото, которое добавляется в галерею
                                    if (selectedPhotoIndex == -1) {
                                        if (galleryPhotos.size < 5) {
                                            galleryPhotos.add(selectedPhoto)
                                        }
                                    } else {
                                        // Иначе обновляем выбранное фото по индексу
                                        galleryPhotos[selectedPhotoIndex!!] = selectedPhoto
                                    }
                                    showGalleryPhotoDialog = false
                                },
                                onDismiss = { showGalleryPhotoDialog = false }
                            )
                        }

                        Button(
                            onClick = {
                                if (name.isBlank() || profession.isBlank() || group.isBlank() || lookingFor.isBlank() || aboutMe.isBlank()) {
                                    errorMessage = "Пожалуйста, заполните все обязательные поля."
                                } else {
                                    errorMessage = null
                                    onSave(
                                        userId,
                                        name,
                                        profession,
                                        group,
                                        mainPhoto,
                                        galleryPhotos,
                                        lookingFor,
                                        aboutMe
                                    )
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

                // Кнопки Назад/Далее
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

fun getDrawableResource(name: String): DrawableResource {
    return when (name) {
        "photo1.jpg" -> Res.drawable.photo1
        "photo2.jpg" -> Res.drawable.photo2
        "photo3.jpg" -> Res.drawable.photo3
        "main_photo_placeholder.jpg" -> Res.drawable.photo4
        else -> Res.drawable.photo5 // дефолт если что-то не то
    }
}

@Composable
fun ChoosePhotoDialog(
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Выберите фото", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "photo1.jpg",
                        "photo2.jpg",
                        "photo3.jpg"
                    ).forEach { photo ->
                        Image(
                            painter = painterResource(getDrawableResource(photo)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSelect(photo)
                                    onDismiss()
                                }
                        )
                    }
                }
            }
        }
    }
}



