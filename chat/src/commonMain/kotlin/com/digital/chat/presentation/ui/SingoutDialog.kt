package com.digital.chat.presentation.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    onDismiss : () -> Unit,
    onConfirm : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Вы уверены?")
        },
        text = {
            Text("Вы точно уверены что хотите удалить диалог? (Все сообщения будут удалены у всех пользователей чата)")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Да")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Нет")
            }
        }
    )
}