package com.example.profile.presentation.editProfileScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    onSave: (
        id: String,
        name: String,
        profession: String,
        group: String,
        mainPhoto: String,
        galleryPhotos: List<String>,
        lookingFor: String,
        aboutMe: String,
        gender: String,
        age: Int,
        status: String,
        specialty: String
    ) -> Unit
) {
    val accentColor = Color(0xFF7F265B)

    // Состояния
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
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val status by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    // Константы
    val steps = listOf("Основное", "Образование", "Дополнительно")
    val lookingForOptions = listOf(
        "Ищу разработчиков", "Ищу друзей", "Никого не ищу, тупо чилю",
        "Ищу киско-жён", "Ищу сигма-мужей"
    )
    val firstOptions = listOf("ИСП", "СИС", "ИБ", "Преподаватель", "Университет", "Поступаю", "Закончил")
    val secondOptions = (1..4).map { it.toString() }
    val thirdOptions = (4..8).map { it.toString() }

    // Состояния UI
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var firstExpanded by remember { mutableStateOf(false) }
    val firstSelected = remember { mutableStateOf(firstOptions.first()) }
    var secondExpanded by remember { mutableStateOf(false) }
    val secondSelected = remember { mutableStateOf(secondOptions.first()) }
    var thirdExpanded by remember { mutableStateOf(false) }
    val thirdSelected = remember { mutableStateOf(thirdOptions.first()) }

    // Логика
    val coroutineScope = rememberCoroutineScope()
    val imagePicker = rememberImagePicker()

    // Обработчики выбора изображений
    val pickMainPhoto = imagePicker.registerPicker(
        onImagePicked = { imagePath -> imagePath?.let { mainPhoto = it } },
        isMainPhoto = true
    )

    val pickGalleryPhoto = imagePicker.registerPicker(
        onImagePicked = { imagePath ->
            imagePath?.let {
                selectedPhotoIndex?.let { index ->
                    if (index == -1 && galleryPhotos.size < 5) {
                        galleryPhotos.add(it)
                    } else if (index != null && index in galleryPhotos.indices) {
                        galleryPhotos[index] = it
                    }
                }
            }
        },
        isMainPhoto = false
    )

    suspend fun uploadAndSaveProfile() {
        onSave(
            userId,
            name,
            profession,
            group,
            mainPhoto,
            galleryPhotos,
            lookingFor,
            aboutMe,
            gender,
            age.toIntOrNull() ?: 0,
            status,
            specialty
        )
    }

    fun updateSpecialty() {
        specialty = firstSelected.value
        group = if (firstSelected.value !in listOf("Преподаватель", "Университет", "Поступаю", "Закончил")) {
            "${secondSelected.value}0${thirdSelected.value}"
        } else {
            ""
        }
    }

    // UI
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
                StepIndicator(
                    index = index,
                    currentStep = step,
                    title = title,
                    accentColor = accentColor
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Main content
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
                    0 -> Step1Basic(
                        accentColor = accentColor,
                        name = name,
                        onNameChange = { name = it },
                        age = age,
                        onAgeChange = { age = it },
                        gender = gender,
                        onGenderChange = { gender = it },
                        mainPhoto = mainPhoto,
                        onPickMainPhoto = pickMainPhoto
                    )
                    1 -> Step2Education(
                        accentColor = accentColor,
                        profession = profession,
                        onProfessionChange = { profession = it },
                        firstOptions = firstOptions,
                        firstExpanded = firstExpanded,
                        onFirstExpandedChange = { firstExpanded = it },
                        firstSelected = firstSelected.value,
                        onFirstSelectedChange = {
                            firstSelected.value = it
                            updateSpecialty()
                        },
                        secondOptions = secondOptions,
                        secondExpanded = secondExpanded,
                        onSecondExpandedChange = { secondExpanded = it },
                        secondSelected = secondSelected.value,
                        onSecondSelectedChange = {
                            secondSelected.value = it
                            updateSpecialty()
                        },
                        thirdOptions = thirdOptions,
                        thirdExpanded = thirdExpanded,
                        onThirdExpandedChange = { thirdExpanded = it },
                        thirdSelected = thirdSelected.value,
                        onThirdSelectedChange = {
                            thirdSelected.value = it
                            updateSpecialty()
                        },
                        updateSpecialty = ::updateSpecialty
                    )
                    2 -> Step3Additional(
                        accentColor = accentColor,
                        lookingForOptions = lookingForOptions,
                        isDropdownExpanded = isDropdownExpanded,
                        onDropdownExpandedChange = { isDropdownExpanded = it },
                        lookingFor = lookingFor,
                        onLookingForChange = { lookingFor = it },
                        aboutMe = aboutMe,
                        onAboutMeChange = { aboutMe = it },
                        galleryPhotos = galleryPhotos,
                        coroutineScope = coroutineScope,
                        name = name,
                        profession = profession,
                        group = group,
                        age = age,
                        errorMessage = errorMessage,
                        onErrorMessageChange = { errorMessage = it },
                        onSave = ::uploadAndSaveProfile,
                        onGalleryItemClick = { index ->
                            selectedPhotoIndex = index
                            pickGalleryPhoto()
                        }
                    )
                }

                NavigationButtons(
                    step = step,
                    steps = steps,
                    accentColor = accentColor,
                    onBack = { step-- },
                    onNext = { step++ }
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(
    index: Int,
    currentStep: Int,
    title: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (index <= currentStep) accentColor else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("${index + 1}", color = Color.White, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            title,
            fontSize = 11.sp,
            color = if (index == currentStep) accentColor else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NavigationButtons(
    step: Int,
    steps: List<String>,
    accentColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (step > 0) {
            OutlinedButton(
                onClick = onBack,
                border = BorderStroke(1.dp, accentColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
            ) {
                Text("Назад")
            }
        }
        if (step < steps.lastIndex) {
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Далее", color = Color.White)
            }
        }
    }
}
@Composable
private fun Step1Basic(
    accentColor: Color,
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    mainPhoto: String,
    onPickMainPhoto: () -> Unit  // Заменяем onPickImage на конкретный обработчик
) {
    Text(
        "Шаг 1: Основное",
        color = accentColor,
        style = MaterialTheme.typography.titleLarge
    )

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("ФИО") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = age,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onAgeChange(newValue)
            }
        },
        label = { Text("Возраст") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )

    Text("Пол", color = accentColor)

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val genders = listOf("М", "Ж")
        genders.forEach { genderOption ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (gender == genderOption),
                        onClick = { onGenderChange(genderOption) }
                    )
            ) {
                RadioButton(
                    selected = (gender == genderOption),
                    onClick = { onGenderChange(genderOption) },
                    colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                )
                Text(text = genderOption)
            }
        }
    }

    Text("Главное фото", style = MaterialTheme.typography.labelLarge)

    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onPickMainPhoto),  // Используем переданный обработчик
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accentColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (mainPhoto.isNotBlank()) {
                AsyncImage(
                    imageUrl = mainPhoto,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить",
                    tint = accentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Education(
    accentColor: Color,
    profession: String,
    onProfessionChange: (String) -> Unit,
    firstOptions: List<String>,
    firstExpanded: Boolean,
    onFirstExpandedChange: (Boolean) -> Unit,
    firstSelected: String,
    onFirstSelectedChange: (String) -> Unit,
    secondOptions: List<String>,
    secondExpanded: Boolean,
    onSecondExpandedChange: (Boolean) -> Unit,
    secondSelected: String,
    onSecondSelectedChange: (String) -> Unit,
    thirdOptions: List<String>,
    thirdExpanded: Boolean,
    onThirdExpandedChange: (Boolean) -> Unit,
    thirdSelected: String,
    onThirdSelectedChange: (String) -> Unit,
    updateSpecialty: () -> Unit
) {
    Text(
        "Шаг 2: Образование",
        color = accentColor,
        style = MaterialTheme.typography.titleLarge
    )

    OutlinedTextField(
        value = profession,
        onValueChange = onProfessionChange,
        label = { Text("Род деятельности") },
        placeholder = { Text("Например: Backend, DevOps, Android") },
        modifier = Modifier.fillMaxWidth()
    )

    ExposedDropdownMenuBox(
        expanded = firstExpanded,
        onExpandedChange = onFirstExpandedChange
    ) {
        OutlinedTextField(
            value = firstSelected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Специальность") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = firstExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = firstExpanded,
            onDismissRequest = { onFirstExpandedChange(false) }
        ) {
            firstOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onFirstSelectedChange(option)
                        onFirstExpandedChange(false)
                        updateSpecialty()
                    }
                )
            }
        }
    }

    if (firstSelected !in listOf("Преподаватель", "Университет", "Поступаю", "Закончил")) {
        Text("Номер группы", color = accentColor)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = secondExpanded,
                onExpandedChange = onSecondExpandedChange,
                modifier = Modifier.width(80.dp)
            ) {
                OutlinedTextField(
                    value = secondSelected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = secondExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = secondExpanded,
                    onDismissRequest = { onSecondExpandedChange(false) }
                ) {
                    secondOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSecondSelectedChange(option)
                                onSecondExpandedChange(false)
                                updateSpecialty()
                            }
                        )
                    }
                }
            }

            Text(
                "0",
                fontSize = 25.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = thirdExpanded,
                onExpandedChange = onThirdExpandedChange,
                modifier = Modifier.width(80.dp)
            ) {
                OutlinedTextField(
                    value = thirdSelected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = thirdExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = thirdExpanded,
                    onDismissRequest = { onThirdExpandedChange(false) }
                ) {
                    thirdOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onThirdSelectedChange(option)
                                onThirdExpandedChange(false)
                                updateSpecialty()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step3Additional(
    accentColor: Color,
    lookingForOptions: List<String>,
    isDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    lookingFor: String,
    onLookingForChange: (String) -> Unit,
    aboutMe: String,
    onAboutMeChange: (String) -> Unit,
    galleryPhotos: List<String>,
    coroutineScope: CoroutineScope,
    name: String,
    profession: String,
    group: String,
    age: String,
    errorMessage: String?,
    onErrorMessageChange: (String?) -> Unit,
    onSave: suspend () -> Unit,
    onGalleryItemClick: (Int) -> Unit
) {
    Text(
        "Шаг 3: Дополнительно",
        color = accentColor,
        style = MaterialTheme.typography.titleLarge
    )

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = onDropdownExpandedChange
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
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownExpandedChange(false) }
        ) {
            lookingForOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onLookingForChange(option)
                        onDropdownExpandedChange(false)
                    }
                )
            }
        }
    }

    OutlinedTextField(
        value = aboutMe,
        onValueChange = onAboutMeChange,
        label = { Text("Обо мне") },
        modifier = Modifier.fillMaxWidth()
    )

    Text("Дополнительные фото", style = MaterialTheme.typography.labelLarge)

    // Галерея фото
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(galleryPhotos) { index, photo ->
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onGalleryItemClick(index) }, // Передаем индекс
                shape = RoundedCornerShape(10.dp)
            ) {
                AsyncImage(
                    imageUrl = photo,
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
                        .clickable { onGalleryItemClick(-1) }, // Передаем -1 для нового фото
                    border = BorderStroke(1.dp, accentColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = accentColor
                        )
                    }
                }
            }
        }
    }

    Button(
        onClick = {
            if (name.isBlank() || profession.isBlank()
                || age.isBlank() || lookingFor.isBlank() || aboutMe.isBlank()
            ) {
                onErrorMessageChange("Пожалуйста, заполните все обязательные поля.")
            } else {
                onErrorMessageChange(null)
                coroutineScope.launch {
                    onSave()
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

// commonMain/kotlin/com/example/image/ImagePicker.kt
interface ImagePicker {
    @Composable
    fun registerPicker(
        onImagePicked: (String?) -> Unit,
        isMainPhoto: Boolean = false
    ): () -> Unit
}

@Composable
expect fun rememberImagePicker(): ImagePicker

@Composable
expect fun AsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
)

expect suspend fun uploadImageToSupabase(
    context: Any,
    userId: String,
    imagePath: String,
    fileName: String,
    oldFilePath: String? = null
): String?





