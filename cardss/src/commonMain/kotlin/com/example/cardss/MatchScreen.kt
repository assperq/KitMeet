package com.example.cardss

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.painterResource

@Composable
fun MatchScreen(
    onSayHi: () -> Unit,
    onKeepSwiping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Задняя карточка (чуть ниже, под задним планом)
            Box(
                modifier = Modifier
                    .offset(x = (-30).dp, y = (-20).dp)
                    .zIndex(0f)
            ) {
                UserCardWithHeart(
                    icon = Icons.Default.Person,
                    rotationDegrees = -30f,
                    heartAlignment = Alignment.TopEnd,
                    heartOffset = DpOffset(60.dp, (-20).dp)
                )
            }

            // Передняя карточка (выше и слева)
            Box(
                modifier = Modifier
                    .offset(x = 70.dp, y = (-70).dp)
                    .zIndex(1f)
            ) {
                UserCardWithHeart(
                    icon = Icons.Default.Person,
                    rotationDegrees = 10f,
                    heartAlignment = Alignment.BottomStart,
                    heartOffset = DpOffset((-20).dp, 160.dp)
                )
            }
        }

        Text(
            text = "ЭТО МЭТЧ, ПОЗДРАВЛЯЮ!",
            color = Color(0xFF800060),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Начните общаться уже сейчас!",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp), // добавили отступы слева и справа
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSayHi,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800060)),
                shape = RoundedCornerShape(8.dp), // менее закруглённые края
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Сказать привет!", color = Color.White)
            }

            OutlinedButton(
                onClick = onKeepSwiping,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Продолжить свайпать")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun UserCardWithHeart(
    icon: ImageVector,
    rotationDegrees: Float,
    heartAlignment: Alignment,
    heartOffset: DpOffset
) {
    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 260.dp)
            .graphicsLayer {
                rotationZ = rotationDegrees
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(Color(0xFFE0E0E0))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp)
        )
    }

    // ВНЕ box — чтобы не обрезалось!
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White, CircleShape)
            .border(1.dp, Color.LightGray, CircleShape)
            .offset(heartOffset.x, heartOffset.y)
            .zIndex(2f), // всегда сверху
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart",
            tint = Color(0xFF800060),
            modifier = Modifier.size(24.dp)
        )
    }
}