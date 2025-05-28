package com.digital.registration.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DrawerDefaults.shape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.digital.registration.presentation.RegistrationViewModel
import com.digital.registration.presentation.StringChecker
import com.digital.registration.presentation.log
import com.digital.registration.presentation.provideRegistrationViewModel
import com.digital.settings.presentation.SettingsViewModel
import com.digital.supabaseclients.SupabaseManager
import com.digital.supabaseclients.SupabaseManager.supabaseClient
import io.github.jan.supabase.auth.auth
import kitmeet.registration.generated.resources.Res
import kitmeet.registration.generated.resources.ic_invisible_password
import kitmeet.registration.generated.resources.ic_ukit_logo
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegistrationScreen(
    settingsViewModel : SettingsViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToAuthenticatedRoute: (email : String, password : String) -> Unit = { _, _ ->},
    registrationViewModel: RegistrationViewModel = provideRegistrationViewModel()
) {
    val supabaseClient = remember { SupabaseManager.supabaseClient }

    var emailText by remember {
        mutableStateOf("")
    }
    var firstPassText by remember {
        mutableStateOf("")
    }
    var secondPassText by remember {
        mutableStateOf("")
    }
    var firstPasswordVisibility by remember {
        mutableStateOf(false)
    }
    var secondPasswordVisibility by remember {
        mutableStateOf(false)
    }

    val registrationState = registrationViewModel.state.collectAsState()

    var showDialog by remember {
        mutableStateOf(false)
    }

    var dialogText by remember {
        mutableStateOf("")
    }

    val showDialogFun = { text: String ->
        showDialog = true
        dialogText = text
        log(dialogText)
    }

    if (showDialog) {
        ErrorDialog(message = dialogText, title = "Ошибка регистрации", onClickOk = { showDialog = false }, onDismiss = { showDialog = false })
    }

    when (registrationState.value) {
        is RegistrationViewModel.State.Error -> {
            showDialogFun((registrationState.value as RegistrationViewModel.State.Error).e.message.toString())
        }
        RegistrationViewModel.State.StartState -> {}
    }

    Container {
        Column {
            Image(painterResource(Res.drawable.ic_ukit_logo), null)
            VerticalSpace()
            BaseText("Регистрация аккаунта", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            VerticalSpace(2.dp)
            BaseText("Готов вступить на тёмную сторону Укита?", color = Color(82, 82, 82), fontSize = 16.sp)
            VerticalSpace()
            val topTextFieldColor = Color(130, 130, 130)
            val placeholderTextColor = Color(224, 224, 224)

            BaseText("Email", color = topTextFieldColor, fontSize = 14.sp)
            OutlinedTextField(
                value = emailText,
                onValueChange = {
                    emailText = it
                },
                placeholder = {
                    BaseText("@mgutu.loc", color = placeholderTextColor, fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            VerticalSpace()
            BaseText("Пароль", color = topTextFieldColor, fontSize = 14.sp)
            OutlinedTextField(
                value = firstPassText,
                onValueChange = {
                    firstPassText = it
                },
                placeholder = {
                    Text("*****************", color = placeholderTextColor, fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (firstPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { firstPasswordVisibility = !firstPasswordVisibility }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_invisible_password),
                            contentDescription = if (firstPasswordVisibility) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                singleLine = true
            )
            VerticalSpace()
            BaseText("Подтвердить пароль", color = topTextFieldColor, fontSize = 14.sp)
            OutlinedTextField(
                value = secondPassText,
                onValueChange = {
                    secondPassText = it
                },
                placeholder = {
                    Text("*****************", color = placeholderTextColor, fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (secondPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { secondPasswordVisibility = !secondPasswordVisibility }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_invisible_password),
                            contentDescription = if (secondPasswordVisibility) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                singleLine = true
            )

            VerticalSpace()

            var checkedState by remember { mutableStateOf(false) }
            var showPolicyDialog by remember { mutableStateOf(false) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = { checkedState = it }
                )
                Text(
                    text = "Я согласен с политикой компании",
                    fontSize = 12.sp,
                    color = Color(161, 161, 161),
                    textAlign = TextAlign.Start,
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showPolicyDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Открыть политику",
                        tint = Color(0xFF7F265B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (showPolicyDialog) {
                AlertDialog(
                    onDismissRequest = { showPolicyDialog = false },
                    title = {
                        Text("Политика компании")
                    },
                    text = {
                        Text("Здесь будет подробно описана политика конфиденциальности и условия использования сервиса...")
                    },
                    confirmButton = {
                        TextButton(onClick = { showPolicyDialog = false }) {
                            Text("ОК")
                        }
                    }
                )
            }

            VerticalSpace(14.dp)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (!checkedState) {
                            showDialogFun("Вы должны согласиться с политикой компании")
                            return@Button
                        }
                        if (firstPassText != secondPassText) {
                            showDialogFun("Пароли должны совпадать")
                            return@Button
                        }
                        if (!StringChecker.checkMailString(emailText)) {
                            showDialogFun("Введите верный email")
                            return@Button
                        }
                        if (!StringChecker.checkPassword(firstPassText)) {
                            showDialogFun("Пароль должен иметь больше 6 символов")
                            return@Button
                        }
                        registrationViewModel.viewModelScope.launch {
                            registrationViewModel.singUp(
                                emailText,
                                firstPassText,
                                onSuccess = {
                                    settingsViewModel.setEmail(emailText)
                                    settingsViewModel.setPassword(firstPassText)
                                    onNavigateToAuthenticatedRoute(emailText, firstPassText)
                                    firstPassText = ""
                                    emailText = ""
                                    secondPassText = ""
                                    checkedState = false
                                }
                            )
                        }
                    },
                    content = {
                        BaseText(
                            "Зарегистрироваться",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(210.dp)
                )
            }
            VerticalSpace()
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row {
                    BaseText(
                        "Уже зарегестрированы?",
                        fontSize = 12.sp,
                        color = Color(130, 130, 130)
                    )
                    HorizontalSpace(4.dp)
                    Text(
                        "Войти в аккаунт",
                        color = Color(127, 38, 91),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigateToLogin() })
                }
            }
        }
    }
}