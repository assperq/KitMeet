package com.example.profile.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kitmeet.profile.generated.resources.Res
import kitmeet.profile.generated.resources.photo1
import kitmeet.profile.generated.resources.photo2
import kitmeet.profile.generated.resources.photo3
import kitmeet.profile.generated.resources.photo4
import kitmeet.profile.generated.resources.photo5
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.max
import kotlin.math.min

@Composable
fun ProfileScreen(
    profile: Profile,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f
    val imageSize = remember { mutableStateOf(IntSize.Zero) }
    var isOverflowing by remember { mutableStateOf(false) }
    var actualLineCount by remember { mutableStateOf(0) }

    var editingField by remember { mutableStateOf<String?>(null) }
    var newValue by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }  // Режим редактирования

    fun resetImage() {
        selectedImage = null
        scale = 1f
        offset = Offset.Zero
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Кнопка назад (если showBackButton == true)
        if (showBackButton) {
            IconButton(
                onClick = { onBackClick() },
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
            // Если нет кнопки назад, показываем кнопку "Изменить" (теперь как иконка)
            IconButton(
                onClick = { isEditMode = !isEditMode },
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

        // Кнопка настроек — всегда справа сверху
        IconButton(
            onClick = { /* TODO: Открыть настройки */ },
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

        KamelImage(
            resource = {
                profile.main_photo?.let { asyncPainterResource(it) }!!
            },
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentScale = ContentScale.Crop
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 130.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    editingField = "name"
                                    newValue = profile.name
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Изменить имя",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
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
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Группа с иконкой сразу после текста
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = groupDisplay,
                                        fontSize = 18.sp,
                                        fontStyle = FontStyle.Italic,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            editingField = "group"
                                            newValue = profile.group
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

                                // Профессия с иконкой сразу после текста
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.profession,
                                        fontSize = 18.sp,
                                        fontStyle = FontStyle.Italic,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            editingField = "profession"
                                            newValue = profile.profession
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
                            }

                            Column(
                                verticalArrangement = Arrangement.Top
                            ) {
                                IconButton(
                                    onClick = { /* Обработчик нажатия на чат */ },
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
                                        imageVector = Icons.Filled.ChatBubble,
                                        contentDescription = "Перейти в чат с этим человеком",
                                        tint = Color(0xFF7F265B),
                                        modifier = Modifier.size(34.dp)
                                    )
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
                                    actualLineCount = it.lineCount
                                    isOverflowing = it.lineCount > 5
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.dp)
                                    .alpha(0f)
                            )
                        }

                        Text(
                            text = profile.about_me,
                            fontSize = 18.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable { if (isOverflowing) isExpanded = !isExpanded }
                                .fillMaxWidth()
                        )

                        // Помести это в самый конец функции `Box { ... }`, НАРУЖУ всех Column/Row
                        if (editingField != null) {
                            AlertDialog(
                                onDismissRequest = { editingField = null },
                                title = {
                                    Text("Изменить ${when (editingField) {
                                        "name" -> "имя"
                                        "group" -> "группу"
                                        "profession" -> "профессию"
                                        else -> ""
                                    }}")
                                },
                                text = {
                                    TextField(
                                        value = newValue,
                                        onValueChange = { newValue = it },
                                        singleLine = true
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        when (editingField) {
                                            "name" -> profile.name = newValue
                                            "group" -> profile.group = newValue
                                            "profession" -> profile.profession = newValue
                                        }
                                        editingField = null
                                        // TODO: вызвать сохранение через ViewModel, если нужно
                                    }) {
                                        Text("Сохранить")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { editingField = null }) {
                                        Text("Отмена")
                                    }
                                }
                            )
                        }

                        if (isOverflowing) {
                            Text(
                                text = if (isExpanded) "Скрыть" else "Открыть полностью",
                                fontSize = 14.sp,
                                color = Color(0xFF7F265B),
                                modifier = Modifier
                                    .clickable { isExpanded = !isExpanded }
                                    .padding(bottom = 12.dp)
                            )
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
                                                    selectedImage = photoUrl
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
                                                    selectedImage = photoUrl
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
    selectedImage?.let { imageUrl ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
            KamelImage(
                resource = asyncPainterResource(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = max(min(scale, maxScale), minScale)
                        scaleY = max(min(scale, maxScale), minScale)
                        translationX = offset.x.coerceIn(
                            -(imageSize.value.width * (scale - 1)) / 2,
                            (imageSize.value.width * (scale - 1)) / 2
                        )
                        translationY = offset.y.coerceIn(
                            -(imageSize.value.height * (scale - 1)) / 2,
                            (imageSize.value.height * (scale - 1)) / 2
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures(
                            panZoomLock = true
                        ) { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                            val maxX = (imageSize.value.width * (newScale - 1)) / 2
                            val maxY = (imageSize.value.height * (newScale - 1)) / 2

                            scale = newScale
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        }
                    }
                    .onSizeChanged { imageSize.value = it }
            )

            IconButton(
                onClick = { resetImage() },
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color(0xFFD2D2D2).copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .size(48.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}