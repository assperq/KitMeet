package com.example.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditProfileScreen(userId: String, onSave: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = profession,
            onValueChange = { profession = it },
            label = { Text("Профессия") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = group,
            onValueChange = { group = it },
            label = { Text("Группа") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            if (name.isNotBlank() && profession.isNotBlank() && group.isNotBlank()) {
                onSave(userId, name, profession, group) // Передаем userId сюда
            }
        }) {
            Text("Сохранить")
        }
    }
}