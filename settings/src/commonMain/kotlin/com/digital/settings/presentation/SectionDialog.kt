package com.digital.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.digital.settings.domain.Theme

@Composable
fun SelectionDialog(
    themeMode: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedModeState by remember {
        mutableStateOf(themeMode)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выбор темы") },
        text = {
            Column {
                Theme.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedModeState = mode }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedModeState == mode,
                            onClick = null
                        )
                        Text(
                            text = when(mode) {
                                Theme.Light -> Theme.Light.russianName
                                Theme.Dark -> Theme.Dark.russianName
                                Theme.System -> Theme.System.russianName
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onThemeSelected(selectedModeState) }) {
                Text("Сохранить")
            }
        }
    )
}