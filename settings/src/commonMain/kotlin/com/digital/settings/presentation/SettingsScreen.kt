package com.digital.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.digital.settings.domain.Settings
import com.digital.settings.domain.Theme
import kitmeet.settings.generated.resources.Res
import kitmeet.settings.generated.resources.ic_alert
import kitmeet.settings.generated.resources.ic_back
import kitmeet.settings.generated.resources.ic_exit
import kitmeet.settings.generated.resources.ic_night_mode
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = provideSettingsViewModel()
) {
    var settings by remember {
        mutableStateOf(Settings(
            false, Theme.System,
            email = "",
            password = ""
        ))
    }
    LaunchedEffect(viewModel) {
        viewModel.setting
            .filterNotNull()
            .distinctUntilChanged { old, new ->
                old.enablePush == new.enablePush && old.theme == new.theme
            }
            .collect { it ->
                settings = it
            }
    }
    var showThemeDialog by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        SingoutDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.singOut()
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
                showDialog = false
            }
        )
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
                iconPlaceholder = Res.drawable.ic_alert,
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

            Spacer(modifier = Modifier.padding(vertical = 2.dp))

            Row {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp, top = 2.dp)
                ) {
                    Icon(painter = painterResource(Res.drawable.ic_exit),
                        contentDescription = null, modifier = Modifier.size(40.dp),
                    )
                }
                SettingElement(
                    text = "Выйти из аккаунта",
                    onElementClick = {
                        showDialog = true
                    },
                )
            }
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