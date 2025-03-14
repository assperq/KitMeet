package com.digital.registration.presintation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import kitmeet.registration.generated.resources.Res
import kitmeet.registration.generated.resources.ic_ukit_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoginScreen() {
    Container {
        Image(painterResource(Res.drawable.ic_ukit_logo), null)
    }
}