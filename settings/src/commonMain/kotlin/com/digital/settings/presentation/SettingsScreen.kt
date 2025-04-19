package com.digital.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.digital.settings.domain.Theme
import kitmeet.settings.generated.resources.Res
import kitmeet.settings.generated.resources.ic_alert
import kitmeet.settings.generated.resources.ic_back
import kitmeet.settings.generated.resources.ic_night_mode
import kitmeet.settings.generated.resources.ic_setting_arrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = provideSettingsViewModel(),
    navController: NavController
) {
    val settings by viewModel.settings.collectAsState()
    var showThemeDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(start = 30.dp, end = 30.dp, top = 25.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Image(painter = painterResource(Res.drawable.ic_back), null)
                }

                Text(
                    text = "Настройки",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.padding(vertical = 20.dp))


            SettingsSection(
                title = "Уведомления",
                iconPlaceholder = Res.drawable.ic_alert, // Замените на ваш ресурс
                content = {
                    SettingElement(
                        text = if (settings.enablePush) "Включены" else "Выключены",
                        onElementClick = {
                            viewModel.setEnablePush(!settings.enablePush)
                        }
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Spacer(modifier = Modifier.padding(vertical = 2.dp))

            SettingsSection(
                title = "Ночной режим",
                iconPlaceholder = Res.drawable.ic_night_mode,
                content = {
                    SettingElement(
                        text = settings.theme.russianName,
                        onElementClick = {
                            showThemeDialog = true
                        }
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }

    if (showThemeDialog) {
        SelectionDialog(
            themeMode = settings.theme,
            onThemeSelected = { mode ->
                viewModel.setTheme(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    iconPlaceholder: DrawableResource,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            ) {
                Icon(painter = painterResource(iconPlaceholder), contentDescription = null)
            }
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 5.dp)
                )

                Spacer(Modifier.padding(vertical = 4.dp))

                content()
            }
        }
    }
}

@Composable
private fun SettingElement(
    text : String,
    onElementClick : () -> Unit
) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(146, 146, 146),
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White)
            .clickable(onClick = onElementClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(70.dp))
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Image(painter = painterResource(Res.drawable.ic_setting_arrow), contentDescription = null)
        }

    }
}


@Composable
private fun SelectionDialog(
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