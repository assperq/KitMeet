package com.digital.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.profile.R

@Composable
@Preview
fun ProfileScreen() {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.compose_multiplatform),
            contentDescription = "Фото профиля",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(200.dp))

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
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Анна Петрова",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7F265B)
                            )
                        )

                        repeat(9) {
                            Text(
                                "Я гэй",
                                modifier = Modifier.padding(vertical = 16.dp),
                                style = TextStyle(
                                    fontSize = 48.sp,
                                )
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
                    color = Color(0xCCD2D2D2),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color(
                    0xFF7F265B
                )
            )
        }
    }
}