package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.profile.data.Profile

@Composable
fun SwipeableCardStack(
    profiles: List<Profile>,
    onSwipeLeft: (Profile) -> Unit,
    onSwipeRight: (Profile) -> Unit,
    onCardClick: (Profile) -> Unit
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
                        onSwipeLeft = { onSwipeLeft(topProfile) },
                        onSwipeRight = { onSwipeRight(topProfile) },
                        onClick = { onCardClick(topProfile) }
                    )
                }
            }
        }
    }
}