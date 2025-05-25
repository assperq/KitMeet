package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    profile: Profile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val rotation by remember { derivedStateOf { (offsetX / 20).coerceIn(-15f, 15f) } }

    val swipeDirection by remember {
        derivedStateOf {
            when {
                offsetX > 100 -> SwipeDirection.RIGHT
                offsetX < -100 -> SwipeDirection.LEFT
                else -> null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .background(
                color = if (!profile.status.isNullOrEmpty()) Color(0xFFFFF176) else Color.Transparent, // жёлтый фон, если есть статус
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp) // пространство между жёлтым фоном и самой карточкой
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 300 -> onSwipeRight()
                                offsetX < -300 -> onSwipeLeft()
                            }
                            offsetX = 0f
                            offsetY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .rotate(rotation)
                .clickable { onClick() }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                KamelImage(
                    resource = {
                        profile.main_photo?.let { asyncPainterResource(it) }!!
                    },
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )

                // 🟡 Статус вверху карточки
                profile.status?.takeIf { it.isNotEmpty() }?.let {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Color(0xFFFFEB3B).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFF6A1B9A), // фиолетовый
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 🔴 Иконка сердечка или крестика при движении
                swipeDirection?.let {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.Center)
                            .background(Color.White, shape = CircleShape)
                            .border(2.dp, Color.Black, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = when (it) {
                                SwipeDirection.RIGHT -> Icons.Default.Favorite
                                SwipeDirection.LEFT -> Icons.Default.Close
                            },
                            contentDescription = null,
                            tint = if (it == SwipeDirection.LEFT) Color.Red else Color(0xFF6A1B9A),
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                // 🔵 Блюр и текст
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .blur(10.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${profile.name}, 19", fontSize = 22.sp, color = Color.White)
                    Text(
                        "${profile.specialty} ${profile.group}",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        profile.profession,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // 🔵 Верхнее полупрозрачное фиолетовое окно — сразу после блюра
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .align(Alignment.BottomEnd) // Позиционируем к низу
                        .offset(y = (-140).dp) // Сдвигаем вверх на высоту блюра
                        .background(Color(0xFF6A1B9A).copy(alpha = 0.8f))
                ) {
                    Text(
                        text = profile.looking_for,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}