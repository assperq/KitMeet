package com.digital.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.profile.R

@Preview
@Composable
fun ProfileScreen() {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    // Обработчик закрытия полноэкранного просмотра
    fun resetImage() {
        selectedImage = null
        scale = 1f
        offset = Offset.Zero
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.main_photo),
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Артём Шины Валерьевич, 19",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )

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

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.photo1),
                                    contentDescription = "Фото 1",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Image(
                                    painter = painterResource(R.drawable.photo2),
                                    contentDescription = "Фото 2",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.photo3),
                                    contentDescription = "Фото 3",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Image(
                                    painter = painterResource(R.drawable.photo4),
                                    contentDescription = "Фото 4",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Image(
                                    painter = painterResource(R.drawable.photo5),
                                    contentDescription = "Фото 5",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .background(
                        color = Color(0xFFD2D2D2).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_back),
                    contentDescription = "Назад",
                    tint = Color(0xFF7F265B),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}