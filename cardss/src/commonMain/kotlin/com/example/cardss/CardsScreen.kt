package com.example.cardss

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.data.Profile
import io.github.jan.supabase.SupabaseClient
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun CardsScreen(
    onProfileClick: (String) -> Unit
) {
    val viewModel = remember { CardsViewModel(SupabaseManager.supabaseClient) }

    val profilesState = viewModel.profiles.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            FilterBottomSheet(
                initialGender = "Оба",
                initialCourse = null,
                initialSpecialization = "Любая"
            ) { gender, course, specialization ->
                println("Пол: $gender, Курс: $course, Спец: $specialization")
                viewModel.loadProfiles(gender, course, specialization)  // <--- ВАЖНО: вызываем фильтрацию
                scope.launch { bottomSheetState.hide() } // скрываем фильтр после применения
            }
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
                profiles = profilesState.value,
                onSwipe = { profile -> viewModel.removeProfile(profile) },
                onCardClick = { profileId ->
                    onProfileClick(profileId.user_id)  // если onProfileClick принимает id
                }
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
                Icons.Default.FilterAlt,
                contentDescription = "Фильтр",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF6A1B9A)
            )
        }
    }
}

@Composable
fun FilterBottomSheet(
    initialGender: String = "М",
    initialCourse: Int? = 1,
    initialSpecialization: String = "ИСП",
    onSave: (gender: String, course: Int?, specialization: String) -> Unit
) {
    var selectedGender by remember { mutableStateOf(initialGender) }
    var selectedCourse by remember { mutableStateOf(initialCourse) }
    var selectedSpecialization by remember { mutableStateOf(initialSpecialization) }

    var specializationExpanded by remember { mutableStateOf(false) }
    val specializationOptions = listOf("ИСП", "СИС", "ИБ", "Преподаватель", "Университет", "Поступаю", "Закончил", "Любая")
    val hideCourseForSpecializations = listOf("Преподаватель", "Университет", "Поступаю", "Закончил")

    val courseOptions = listOf(1, 2, 3, 4, null) // null = Все

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 130.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Фильтры", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

        // Пол
        Text("Пол", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEDE7F6))
                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
        ) {
            listOf("М", "Ж", "Оба").forEach { gender ->
                val selected = selectedGender == gender
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) Color(0xFF6A1B9A) else Color.Transparent)
                        .clickable { selectedGender = gender }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        gender,
                        color = if (selected) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Специальность
        Text("Специальность", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { specializationExpanded = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedSpecialization,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = specializationExpanded,
                onDismissRequest = { specializationExpanded = false }
            ) {
                specializationOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedSpecialization = option
                            specializationExpanded = false
                        }
                    )
                }
            }
        }

        // Курс
        if (selectedSpecialization !in hideCourseForSpecializations) {
            Spacer(modifier = Modifier.height(32.dp))

            Text("Курс", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEDE7F6))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(50))
            ) {
                courseOptions.forEach { course ->
                    val selected = selectedCourse == course
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Color(0xFF6A1B9A) else Color.Transparent)
                            .clickable { selectedCourse = course }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = course?.toString() ?: "Все",
                            color = if (selected) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Сохранить
        Button(
            onClick = {
                onSave(selectedGender, selectedCourse, selectedSpecialization)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Сохранить", color = Color.White)
        }
    }
}

@Composable
fun SwipeableCardStack(
    profiles: List<Profile>,
    onSwipe: (Profile) -> Unit,
    onCardClick: (Profile) -> Unit  // добавляем параметр onCardClick
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
            val topProfile = profiles.lastOrNull()
            if (topProfile != null) {
                key(topProfile.user_id) {
                    SwipeableCard(
                        profile = topProfile,
                        onSwipeLeft = { onSwipe(topProfile) },
                        onSwipeRight = { onSwipe(topProfile) },
                        onClick = { onCardClick(topProfile) }
                    )
                }
            }
        }
    }
}

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
                        .background(Color(0xFFFFEB3B).copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp))
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
                    .height(120.dp)
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
                Text("${profile.name}, 19", fontSize = 26.sp, color = Color.White)
                Text("${profile.specialty} ${profile.group}", fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
                Text(profile.profession, fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
            }

            // 🔵 Верхнее полупрозрачное фиолетовое окно — сразу после блюра
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp)
                    .align(Alignment.BottomEnd) // Позиционируем к низу
                    .offset(y = (-120).dp) // Сдвигаем вверх на высоту блюра
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

enum class SwipeDirection {
    LEFT, RIGHT
}

