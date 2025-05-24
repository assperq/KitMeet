package com.digital.chat.presentation.ui.find

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.digital.chat.presentation.getDateFromInstant
import com.digital.chat.presentation.ui.BaseText
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun FindElement(profile: Profile, navController: NavController, onLikeClick : () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val userId = profile.user_id
                navController.navigate("profileDetails/$userId")
            }
            .padding(vertical = 8.dp, horizontal = 12.dp), // немного отступов
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            KamelImage(
                resource = { asyncPainterResource(profile.main_photo) },
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(10.dp))

            BaseText(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4
            )
        }

        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Предложить мэтч",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6A1B9A)
            )
        }
    }

    Spacer(Modifier.padding(vertical = 6.dp))
}