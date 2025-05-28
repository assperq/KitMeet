package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.data.Profile
import io.github.jan.supabase.storage.storage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay

@Composable
fun MatchScreen(
    currentUserProfile: Profile?,
    matchedProfile: Profile?,
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

        // Блок с карточками
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            // Задняя карточка
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = (-60).dp)
                    .zIndex(0f)
            ) {
                ProfileCardWithHeart(
                    profile = currentUserProfile,
                    rotationDegrees = -20f,
                    heartOffset = DpOffset(30.dp, 220.dp)
                )
            }

            // Передняя карточка
            Box(
                modifier = Modifier
                    .offset(x = 70.dp, y = (-100).dp)
                    .zIndex(1f)
            ) {
                ProfileCardWithHeart(
                    profile = matchedProfile,
                    rotationDegrees = 10f,
                    heartOffset = DpOffset(120.dp, 0.dp)
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
            text = "Вы понравились друг другу!",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = currentUserProfile?.name ?: "Вы",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = matchedProfile?.name ?: "Другой пользователь",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSayHi,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800060)),
                shape = RoundedCornerShape(8.dp),
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
fun ProfileCardWithHeart(
    profile: Profile?,
    rotationDegrees: Float,
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
            .background(Color.LightGray)
    ) {
        val imageUrl = profile?.main_photo
        if (imageUrl != null) {
            KamelImage(
                resource = asyncPainterResource(imageUrl),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset(heartOffset.x, heartOffset.y)
            .size(60.dp)
            .background(Color.White, CircleShape)
            .border(1.dp, Color.Gray, CircleShape)
            .zIndex(2f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart",
            tint = Color(0xFF800060),
            modifier = Modifier.size(40.dp)
        )
    }
}