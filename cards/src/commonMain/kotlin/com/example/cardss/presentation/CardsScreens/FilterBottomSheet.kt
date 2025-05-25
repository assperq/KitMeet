package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val specializationOptions =
        listOf("ИСП", "СИС", "ИБ", "Преподаватель", "Университет", "Поступаю", "Закончил", "Любая")
    val hideCourseForSpecializations =
        listOf("Преподаватель", "Университет", "Поступаю", "Закончил")

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

        Text(
            "Фильтры",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

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
