package com.example.cardss

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun CardsScreen() {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val visibleProfiles = remember(key1 = Unit) {
        mutableStateListOf(
            Profile(1, "Артём Шины Валерьевич", "19", "DevOps, SRE", "ИСП-304"),
            Profile(2, "Андрей Анонимус Сергеевич", "17", "ДНД", "СИС-206"),
            Profile(3, "Мария Иванова", "20", "Фронтенд-разработка", "ИС-101"),
            Profile(4, "Анна Иванова", "20", "Фронтенд-разработка", "ИС-101"),
            Profile(5, "Иван Иванов", "20", "Фронтенд-разработка", "ИС-101")
        )
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            FilterBottomSheet()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7F6))
        ) {
            TopBar(onFilterClick = {
                scope.launch { bottomSheetState.show() }
            })

            Spacer(modifier = Modifier.height(16.dp))

            SwipeableCardStack(
                profiles = visibleProfiles,
                onSwipe = { profile -> visibleProfiles.removeAll { it.id == profile.id } }
            )
        }
    }
}

@Composable
fun TopBar(onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6A1B9A)
            )
        }

        Column(
            modifier = Modifier.weight(1f), // Выровняли по ширине
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "КАРТЫ ТАРО",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Карточек за сегодня: 10",
                fontSize = 18.sp
            )
        }

        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Фильтр",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6A1B9A)
            )
        }
    }
}


@Composable
fun FilterBottomSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Фильтры", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Пол", fontSize = 16.sp)
        Row {
            listOf("М", "Ж", "Оба").forEach { gender ->
                Button(
                    onClick = { /* Выбор пола */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(gender)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Курс", fontSize = 16.sp)
        Row {
            (1..4).forEach { course ->
                Button(
                    onClick = { /* Выбор курса */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(course.toString())
                }
            }
        }
    }
}

@Composable
fun SwipeableCardStack(
    profiles: List<Profile>,
    onSwipe: (Profile) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profiles.isEmpty()) {
            Text(
                text = "Нет карт",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val topProfile = profiles.last()
            SwipeableCard(
                profile = topProfile,
                onSwipeLeft = { onSwipe(topProfile) },
                onSwipeRight = { onSwipe(topProfile) }
            )
        }
    }
}

@Composable
fun SwipeableCard(
    profile: Profile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
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
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star Icon",
                modifier = Modifier.align(Alignment.Center)
            )

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
                        tint = if (it == SwipeDirection.LEFT) Color.Red else Color.Green,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Блюр и текст
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .blur(10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text("${profile.name}, ${profile.age}", fontSize = 22.sp, color = Color.White)
                Text(profile.group, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
                Text(profile.specialization, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

enum class SwipeDirection {
    LEFT, RIGHT
}

data class Profile(
    val id: Int,
    val name: String,
    val age: String,
    val specialization: String,
    val group: String
)




