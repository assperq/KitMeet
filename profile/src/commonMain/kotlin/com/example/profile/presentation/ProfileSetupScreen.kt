package com.example.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileSetupScreen(onComplete: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val viewModel: ProfileViewModel = viewModel()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Настройка профиля")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Возраст") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val ageInt = age.toIntOrNull() ?: 0
                if (ageInt > 0 && name.isNotBlank()) {
                    viewModel.saveProfile(name, ageInt, onComplete)
                }
            },
        ) {
            Text("Сохранить")
        }
    }
}