package com.digital.registration.presentation.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorDialog(message: String, title: String, onClickOk : () -> Unit, onDismiss : () -> Unit) {
    AlertDialog(
        onDismissRequest = { onClickOk() },
        title = { Text(title) },
        text = { Text(message, color = Color.Black, fontSize = 16.sp) },
        confirmButton = {
            Button({ onClickOk() }) {
                Text("OK", fontSize = 22.sp)
            }
        }
    )
}