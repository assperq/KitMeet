package com.example.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kitmeet.profile.generated.resources.Res
import kitmeet.profile.generated.resources.main_photo
import kitmeet.profile.generated.resources.photo1
import kitmeet.profile.generated.resources.photo2
import kitmeet.profile.generated.resources.photo3
import kitmeet.profile.generated.resources.photo4
import kitmeet.profile.generated.resources.photo5
import kotlin.math.max
import kotlin.math.min
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ProfileScreen() {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<DrawableResource?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val minScale = 1f
    val maxScale = 5f
    val imageSize = remember { mutableStateOf(IntSize.Zero) }

    fun resetImage() {
        selectedImage = null
        scale = 1f
        offset = Offset.Zero
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { /* Навигация назад */ },
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

        Image(
            painter = painterResource(Res.drawable.main_photo),
            contentDescription = "Фото профиля",
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

                        Text(
                            "Артём Шины Валерьевич, 19",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )

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
                                Text(
                                    "ИСП-304",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Italic
                                )

                                Text(
                                    "DevOps, SRE",
                                    fontSize = 18.sp,
                                    fontStyle = FontStyle.Italic
                                )

                                Text(
                                    text = "Ищу разработчиков",
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

                        Text(
                            text = "Привет! Меня зовут Артём Егоров, и я работаю DevOps-инженером в компании, которая занимается разработкой и производством шин. Если коротко: моя миссия — сделать так, чтобы технологии \"катились\" без проколов.\n" +
                                    "Чем живу вне работы?\u2028В свободное время я погружаюсь в мир стратегических игр, где с упоением воссоздаю СССР. Для меня это не просто развлечение, а способ изучать историю, архитектуру и сложные системы управления.\u2028Ищу девушку, которая:\n" +
                                    "Не боится носить кепки (и знает, как их сочетать с чем угодно),\n" +
                                    "Разделяет интерес к технологиям или хотя бы не злится, когда я рассказываю про Kubernetes,\n" +
                                    "Готова к спонтанным дискуссиям о том, \"как правильно строить метро в виртуальном Новосибирске\".\n" +
                                    "Если ты любишь пикники под гитарные рифы, ночные забеги по Лона РПГ или просто хочешь обсудить, почему DevOps и квадробинг — это круто, давай знакомиться!", // Полный текст
                            fontSize = 18.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { isExpanded = !isExpanded }
                        )

                        Text(
                            text = if (isExpanded) "Скрыть" else "Открыть полностью",
                            fontSize = 14.sp,
                            color = Color(0xFF7F265B),
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(bottom = 12.dp)
                        )

                        Text(
                            "Галерея:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Первый ряд
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(Res.drawable.photo1, Res.drawable.photo2).forEach { imageRes ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(150.dp)
                                            .clickable { selectedImage = imageRes }
                                    ) {
                                        Image(
                                            painter = painterResource(imageRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }

                            // Второй ряд
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(Res.drawable.photo3, Res.drawable.photo4, Res.drawable.photo5).forEach { imageRes ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(100.dp)
                                            .clickable { selectedImage = imageRes }
                                    ) {
                                        Image(
                                            painter = painterResource(imageRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    selectedImage?.let { imageRes ->
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
            Image(
                painter = painterResource(imageRes),
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
