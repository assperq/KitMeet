package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    onAcceptedClick: () -> Unit,
    onRejectedClick: () -> Unit,
    onFilterClick: () -> Unit,
    cardsSwiped: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левая группа с фиксированной шириной
        Row(
            modifier = Modifier.width(70.dp), // чуть увеличил ширину для иконок
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onAcceptedClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Принятые",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF6A1B9A)
                )
            }

            IconButton(
                onClick = onRejectedClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Отклонённые",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF6A1B9A)
                )
            }
        }

        // Центрируем контент через Box с весом, внутри — Column с минимальным размером
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = "КАРТЫ ТАРО",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Карточек за сегодня: $cardsSwiped",
                    fontSize = 16.sp
                )
            }
        }

        // Правая иконка фильтра с фиксированной шириной, чтобы центр не сдвигался
        Box(
            modifier = Modifier.width(70.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.FilterAlt,
                    contentDescription = "Фильтр",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF6A1B9A)
                )
            }
        }
    }
}