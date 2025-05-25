package com.example.profile.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profile: Profile,
    viewModel: ProfileViewModel,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    navController : NavController
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f
    var isOverflowing by remember { mutableStateOf(false) }
    var actualLineCount by remember { mutableStateOf(0) }
    var editingField by remember { mutableStateOf<String?>(null) }
    var newValue by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    val profile2 = viewModel.currentProfile.collectAsState().value

    val oldPath = profile.main_photo?.let { extractStoragePath(it) }

    val launchImagePicker = pickImageFromGallery(
        userId = profile.user_id,
        oldFilePath = oldPath,
        onImageUploaded = { newUrl ->
            newUrl?.let {
                viewModel.updateMainPhoto(it)
            }
        }
    )

    fun resetImage() {
        selectedImage = null
        scale = 1f
        offset = Offset.Zero
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 2. Верхняя панель
        ProfileTopAppBar(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            isEditMode = isEditMode,
            onLaunchImagePicker = { launchImagePicker() },  // вот сюда
            onEditToggle = {
                if (isEditMode && profile2 != null) {
                    viewModel.viewModelScope.launch {
                        viewModel.saveProfile(
                            userId = profile.user_id,
                            name = profile.name,
                            profession = profile.profession,
                            group = profile.group,
                            mainPhoto = profile.main_photo,
                            galleryPhotos = profile.gallery_photos,
                            lookingFor = profile.looking_for,
                            aboutMe = profile.about_me,
                            gender = profile.gender,
                            age = profile.age,
                            status = profile.status,
                            specialty = profile.specialty
                        )
                    }
                }
                isEditMode = !isEditMode
            }
        )

        // 💥 ФИКСИРОВАННОЕ ИЗОБРАЖЕНИЕ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .zIndex(0f) // Самый задний слой
        ) {
            KamelImage(
                resource = { asyncPainterResource(profile.main_photo) },
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 3. Основные данные
        ProfileContent(
            profile = profile,
            scrollState = scrollState,
            isExpanded = isExpanded,
            isOverflowing = isOverflowing,
            actualLineCount = actualLineCount,
            isEditMode = isEditMode,
            editingField = editingField,
            newValue = newValue,
            onExpandedChange = { isExpanded = it },
            onActualLineCountChange = { actualLineCount = it },
            onOverflowingChange = { isOverflowing = it },
            onEditingFieldChange = { editingField = it },
            onNewValueChange = { newValue = it },
            onImageSelected = { selectedImage = it },
            onLaunchImagePicker = { launchImagePicker() }, // 👈 Вот это
            showBackButton = showBackButton,
            navController = navController
        )

        // 4. Логика галереи
        selectedImage?.let { imageUrl ->
            ExpandedImageOverlay(
                imageUrl = imageUrl,
                minScale = minScale,
                maxScale = maxScale,
                onResetImage = { resetImage() },
                initialScale = scale,
                initialOffset = offset
            )
        }
    }
}


@Composable
private fun ProfileTopAppBar(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    isEditMode: Boolean,
    onLaunchImagePicker: () -> Unit,
    onEditToggle: () -> Unit
) {
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
                onClick = onLaunchImagePicker,
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
            // Кнопка настроек
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    profile: Profile,
    scrollState: ScrollState,
    isExpanded: Boolean,
    isOverflowing: Boolean,
    actualLineCount: Int,
    isEditMode: Boolean,
    editingField: String?,
    newValue: String,
    onExpandedChange: (Boolean) -> Unit,
    onActualLineCountChange: (Int) -> Unit,
    onOverflowingChange: (Boolean) -> Unit,
    onEditingFieldChange: (String?) -> Unit,
    onNewValueChange: (String) -> Unit,
    onImageSelected: (String) -> Unit,
    onLaunchImagePicker: () -> Unit,
    showBackButton: Boolean,
    navController : NavController
) {
    val lookingForOptions = listOf(
        "Ищу разработчиков",
        "Ищу друзей",
        "Никого не ищу, тупо чилю",
        "Ищу киско-жён",
        "Ищу сигма-мужей"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .zIndex(1f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(300.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
                        clip = true
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 130.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        if (isEditMode) {
                            IconButton(
                                onClick = {
                                    onEditingFieldChange("name_and_age") // специальный ключ для редактирования сразу имени и возраста
                                    // Инициализируем newValue значениями в формате "name|age" (можно так или отдельные переменные)
                                    onNewValueChange("${profile.name}|${profile.age}")
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Изменить имя и возраст",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    val groupDisplay = if (profile.specialty in listOf(
                            "Университет",
                            "Поступаю",
                            "Закончил"
                        )
                    ) {
                        profile.specialty
                    } else {
                        "${profile.specialty}-${profile.group}"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Группа с иконкой сразу после текста
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = groupDisplay,
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Italic,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isEditMode) {
                                    IconButton(
                                        onClick = {
                                            onEditingFieldChange("group")
                                            onNewValueChange(profile.group)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Изменить группу",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                            }

                            // Профессия с иконкой сразу после текста
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.profession,
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Italic,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isEditMode) {
                                    IconButton(
                                        onClick = {
                                            onEditingFieldChange("profession")
                                            onNewValueChange(profile.profession)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Изменить профессию",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Цель знакомства, без иконки (как было)
                                Text(
                                    text = profile.looking_for,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF7F265B),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                if (isEditMode) {
                                    IconButton(
                                        onClick = {
                                            onEditingFieldChange("looking_for")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Изменить кого ищет",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }


                        Column(
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.People, // Иконка друзей (можно заменить на нужную)
                                    contentDescription = "Количество друзей",
                                    tint = Color(0xFF7F265B),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "12", // Здесь число друзей, можно заменить на переменную
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (showBackButton) {
                                Spacer(modifier = Modifier.height(8.dp))

                                IconButton(
                                    onClick = { navController.navigate("selectedChat/${profile.user_id}") },
                                    modifier = Modifier
                                        .size(52.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF7F265B),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Message,
                                        contentDescription = "Перейти в чат с этим человеком",
                                        tint = Color(0xFF7F265B),
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        "Обо мне:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    if (actualLineCount == 0) {
                        Text(
                            text = profile.about_me,
                            fontSize = 18.sp,
                            maxLines = Int.MAX_VALUE,
                            onTextLayout = {
                                onActualLineCountChange(it.lineCount)
                                onOverflowingChange(it.lineCount > 5)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.dp)
                                .alpha(0f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = profile.about_me,
                            fontSize = 18.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable { if (isOverflowing) onExpandedChange(!isExpanded) }
                                .weight(1f) // занимает доступное пространство
                        )

                        if (isEditMode) {
                            IconButton(
                                onClick = {
                                    onEditingFieldChange("about_me")
                                    onNewValueChange(profile.about_me)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Изменить обо мне",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (editingField != null) {
                        when (editingField) {
                            "name_and_age" -> {
                                val parts = newValue.split("|")
                                var tempName by remember {
                                    mutableStateOf(
                                        parts.getOrNull(0) ?: ""
                                    )
                                }
                                var tempAge by remember {
                                    mutableStateOf(
                                        parts.getOrNull(1) ?: ""
                                    )
                                }

                                AlertDialog(
                                    onDismissRequest = { onEditingFieldChange(null) },
                                    title = { Text("Изменить имя и возраст") },
                                    text = {
                                        Column {
                                            TextField(
                                                value = tempName,
                                                onValueChange = { tempName = it },
                                                label = { Text("Имя") },
                                                singleLine = true,
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = tempAge,
                                                onValueChange = {
                                                    tempAge = it.filter { ch -> ch.isDigit() }
                                                },
                                                label = { Text("Возраст") },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            profile.name = tempName
                                            profile.age = tempAge.toIntOrNull() ?: profile.age
                                            onEditingFieldChange(null)
                                        }) { Text("Сохранить") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { onEditingFieldChange(null) }) {
                                            Text(
                                                "Отмена"
                                            )
                                        }
                                    }
                                )
                            }

                            "group" -> {
                                var specialtyTemp by remember { mutableStateOf(profile.specialty) }
                                var groupTemp by remember { mutableStateOf(profile.group) }

                                val firstOptions = listOf(
                                    "ИСП",
                                    "СИС",
                                    "ИБ",
                                    "Преподаватель",
                                    "Университет",
                                    "Поступаю",
                                    "Закончил"
                                )
                                val secondOptions = (1..4).map { it.toString() }
                                val thirdOptions = (1..8).map { it.toString() }

                                var firstExpanded by remember { mutableStateOf(false) }
                                var secondExpanded by remember { mutableStateOf(false) }
                                var thirdExpanded by remember { mutableStateOf(false) }

                                val firstSelected = remember {
                                    mutableStateOf(firstOptions.find {
                                        specialtyTemp.contains(it)
                                    } ?: firstOptions.first())
                                }
                                val secondSelected = remember {
                                    mutableStateOf(
                                        groupTemp.getOrNull(0)?.toString() ?: "1"
                                    )
                                }
                                val thirdSelected = remember {
                                    mutableStateOf(
                                        groupTemp.getOrNull(2)?.toString() ?: "1"
                                    )
                                }

                                fun updateTempGroup() {
                                    specialtyTemp = firstSelected.value
                                    groupTemp = if (firstSelected.value !in listOf(
                                            "Университет",
                                            "Поступаю",
                                            "Закончил"
                                        )
                                    ) {
                                        "${secondSelected.value}0${thirdSelected.value}"
                                    } else {
                                        ""
                                    }
                                }

                                AlertDialog(
                                    onDismissRequest = { onEditingFieldChange(null) },
                                    title = { Text("Изменить группу") },
                                    text = {
                                        Column {
                                            // Специальность
                                            ExposedDropdownMenuBox(
                                                expanded = firstExpanded,
                                                onExpandedChange = {
                                                    firstExpanded = !firstExpanded
                                                }
                                            ) {
                                                OutlinedTextField(
                                                    value = firstSelected.value,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("Специальность") },
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = firstExpanded
                                                        )
                                                    },
                                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                                )

                                                ExposedDropdownMenu(
                                                    expanded = firstExpanded,
                                                    onDismissRequest = { firstExpanded = false }
                                                ) {
                                                    firstOptions.forEach { option ->
                                                        DropdownMenuItem(
                                                            text = { Text(option) },
                                                            onClick = {
                                                                firstSelected.value = option
                                                                firstExpanded = false
                                                                updateTempGroup()
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            if (firstSelected.value !in listOf(
                                                    "Университет",
                                                    "Поступаю",
                                                    "Закончил"
                                                )
                                            ) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    // Первая цифра
                                                    ExposedDropdownMenuBox(
                                                        expanded = secondExpanded,
                                                        onExpandedChange = {
                                                            secondExpanded = !secondExpanded
                                                        },
                                                        modifier = Modifier.width(80.dp)
                                                    ) {
                                                        OutlinedTextField(
                                                            value = secondSelected.value,
                                                            onValueChange = {},
                                                            readOnly = true,
                                                            label = { Text("Группа") },
                                                            trailingIcon = {
                                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                                    expanded = secondExpanded
                                                                )
                                                            },
                                                            modifier = Modifier.fillMaxWidth()
                                                                .menuAnchor()
                                                        )
                                                        ExposedDropdownMenu(
                                                            expanded = secondExpanded,
                                                            onDismissRequest = {
                                                                secondExpanded = false
                                                            }
                                                        ) {
                                                            secondOptions.forEach { option ->
                                                                DropdownMenuItem(
                                                                    text = { Text(option) },
                                                                    onClick = {
                                                                        secondSelected.value =
                                                                            option
                                                                        secondExpanded = false
                                                                        updateTempGroup()
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Text(
                                                        "0",
                                                        fontSize = 24.sp,
                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                    )

                                                    // Вторая цифра
                                                    ExposedDropdownMenuBox(
                                                        expanded = thirdExpanded,
                                                        onExpandedChange = {
                                                            thirdExpanded = !thirdExpanded
                                                        },
                                                        modifier = Modifier.width(80.dp)
                                                    ) {
                                                        OutlinedTextField(
                                                            value = thirdSelected.value,
                                                            onValueChange = {},
                                                            readOnly = true,
                                                            label = { Text("Группа") },
                                                            trailingIcon = {
                                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                                    expanded = thirdExpanded
                                                                )
                                                            },
                                                            modifier = Modifier.fillMaxWidth()
                                                                .menuAnchor()
                                                        )
                                                        ExposedDropdownMenu(
                                                            expanded = thirdExpanded,
                                                            onDismissRequest = {
                                                                thirdExpanded = false
                                                            }
                                                        ) {
                                                            thirdOptions.forEach { option ->
                                                                DropdownMenuItem(
                                                                    text = { Text(option) },
                                                                    onClick = {
                                                                        thirdSelected.value = option
                                                                        thirdExpanded = false
                                                                        updateTempGroup()
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            profile.specialty = specialtyTemp
                                            profile.group = groupTemp
                                            onEditingFieldChange(null)
                                        }) {
                                            Text("Сохранить")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { onEditingFieldChange(null) }) {
                                            Text("Отмена")
                                        }
                                    }
                                )
                            }

                            "profession" -> {
                                AlertDialog(
                                    onDismissRequest = { onEditingFieldChange(null) },
                                    title = {
                                        Text(
                                            "Изменить ${
                                                when (editingField) {
                                                    "group" -> "группу"
                                                    "profession" -> "профессию"
                                                    else -> ""
                                                }
                                            }"
                                        )
                                    },
                                    text = {
                                        TextField(
                                            value = newValue,
                                            onValueChange = { onNewValueChange(it) },
                                            singleLine = true
                                        )
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            when (editingField) {
                                                "group" -> profile.group = newValue
                                                "profession" -> profile.profession = newValue
                                            }
                                            onEditingFieldChange(null)
                                        }) { Text("Сохранить") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { onEditingFieldChange(null) }) {
                                            Text(
                                                "Отмена"
                                            )
                                        }
                                    }
                                )
                            }

                            "looking_for" -> {
                                var tempLookingFor by remember { mutableStateOf(profile.looking_for) }
                                var expanded by remember { mutableStateOf(false) }

                                AlertDialog(
                                    onDismissRequest = {
                                        onEditingFieldChange(null)
                                    },
                                    title = { Text("Кого ищет") },
                                    text = {
                                        Column {
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = { expanded = !expanded }
                                            ) {
                                                OutlinedTextField(
                                                    value = tempLookingFor,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("Кого ищет") },
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = expanded
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .menuAnchor() // ✅ ОБЯЗАТЕЛЬНО ДЛЯ DROPDOWN внутри диалога
                                                )

                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false }
                                                ) {
                                                    lookingForOptions.forEach { option ->
                                                        DropdownMenuItem(
                                                            text = { Text(option) },
                                                            onClick = {
                                                                tempLookingFor = option
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            profile.looking_for = tempLookingFor
                                            onEditingFieldChange(null)
                                        }) {
                                            Text("Сохранить")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { onEditingFieldChange(null) }) {
                                            Text("Отмена")
                                        }
                                    }
                                )
                            }

                            "about_me" -> {
                                var tempAboutMe by remember { mutableStateOf(newValue) }

                                AlertDialog(
                                    onDismissRequest = { onEditingFieldChange(null) },
                                    title = { Text("Изменить 'Обо мне'") },
                                    text = {
                                        TextField(
                                            value = tempAboutMe,
                                            onValueChange = { tempAboutMe = it },
                                            modifier = Modifier.height(150.dp),
                                            singleLine = false,
                                            maxLines = 10,
                                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                                        )
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            profile.about_me = tempAboutMe
                                            onEditingFieldChange(null)
                                        }) {
                                            Text("Сохранить")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { onEditingFieldChange(null) }) {
                                            Text("Отмена")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        "Галерея:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    profile.gallery_photos?.let { photos ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Первая строка — 2 фото
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                photos.take(2).forEach { photoUrl ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray)
                                            .clickable {
                                                onImageSelected(photoUrl)
                                            }
                                    ) {
                                        KamelImage(
                                            { asyncPainterResource(photoUrl) },
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                // Если меньше 2 фото, добавляем заглушки
                                repeat(2 - photos.take(2).size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }

                            // Вторая строка — 3 фото
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                photos.drop(2).take(3).forEach { photoUrl ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray)
                                            .clickable {
                                                onImageSelected(photoUrl)
                                            }
                                    ) {
                                        KamelImage(
                                            { asyncPainterResource(photoUrl) },
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                // Если меньше 3 фото, добавляем заглушки
                                repeat(3 - photos.drop(2).take(3).size) {
                                    Spacer(modifier = Modifier.weight(1f))

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedImageOverlay(
    imageUrl: String,
    initialScale: Float,
    initialOffset: Offset,
    onResetImage: () -> Unit,
    minScale: Float = 1f,
    maxScale: Float = 5f,
) {
    var scale by remember { mutableStateOf(initialScale) }
    var offset by remember { mutableStateOf(initialOffset) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(3f)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > minScale) minScale else 2f
                        offset = Offset.Zero
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures(panZoomLock = true) { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                        val maxX = (imageSize.width * (newScale - 1)) / 2
                        val maxY = (imageSize.height * (newScale - 1)) / 2

                        scale = newScale
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                            y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                        )
                    }
                }
        ) {
            KamelImage(
                resource = asyncPainterResource(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .onSizeChanged { imageSize = it }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
        }

        IconButton(
            onClick = {
                scale = 1f
                offset = Offset.Zero
                onResetImage()
            },
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Закрыть",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

fun extractStoragePath(publicUrl: String): String? {
    val prefix = "https://kmehxgdlljbtrfnlzbgr.supabase.co/storage/v1/object/public/"
    return if (publicUrl.startsWith(prefix)) {
        publicUrl.removePrefix(prefix)
    } else null
}