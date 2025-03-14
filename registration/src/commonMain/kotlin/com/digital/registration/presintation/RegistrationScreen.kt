package com.digital.registration.presintation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kitmeet.registration.generated.resources.Res
import kitmeet.registration.generated.resources.ic_ukit_logo
import org.jetbrains.compose.resources.painterResource
import org.w3c.dom.Text

@Composable
fun RegistrationScreen() {
    Container {
        Column {
            Image(painterResource(Res.drawable.ic_ukit_logo), null)
            VerticalSpace()
            Text("Регистрация аккаунта", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            VerticalSpace(2.dp)
            Text("Готовы начать оттерабанивать?", color = Color(82,82,82), fontSize = 16.sp)
            VerticalSpace()
            val elGray = Color(130,130,130)
        }
    }
}