package com.digital.registration.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import com.digital.registration.presentation.RegistrationViewModel
import com.digital.registration.presentation.StringChecker
import com.digital.registration.presentation.provideRegistrationViewModel
import com.digital.settings.presentation.SettingsViewModel
import kitmeet.registration.generated.resources.Res
import kitmeet.registration.generated.resources.ic_invisible_password
import kitmeet.registration.generated.resources.ic_ukit_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoginScreen(
    settingsViewModel : SettingsViewModel,
    onNavigateToRegistration: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToAuthenticatedRoute: (email : String, password : String) -> Unit = { _, _ ->},
    registrationViewModel: RegistrationViewModel = provideRegistrationViewModel()
) {
    var emailText by remember {
        mutableStateOf("")
    }
    var firstPassText by remember {
        mutableStateOf("")
    }
    var firstPasswordVisibility by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var dialogText by remember {
        mutableStateOf("")
    }

    val showDialogFun = { text: String ->
        showDialog = true
        dialogText = text
    }

    if (showDialog) {
        ErrorDialog(message = dialogText, title = "Ошибка авторизации", onClickOk = { showDialog = false }, onDismiss = { showDialog = false })
    }

    val registrationState = registrationViewModel.state.collectAsState()

    when (registrationState.value) {
        is RegistrationViewModel.State.Error -> {
            showDialog = true
            dialogText = (registrationState.value as RegistrationViewModel.State.Error).e.message.toString()
        }
        RegistrationViewModel.State.StartState -> {}
    }

    Container {
        Column {
            Image(painterResource(Res.drawable.ic_ukit_logo), null)
            VerticalSpace()
            BaseText("Вход в аккаунт", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            VerticalSpace(2.dp)
            BaseText(
                "Готовы снова поучвствовать тёмную сторону Укита?",
                color = Color(82, 82, 82),
                fontSize = 16.sp
            )
            VerticalSpace(10.dp)
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

            VerticalSpace(10.dp)
            var checkedState by remember {
                mutableStateOf(false)
            }
            Row(
                modifier = Modifier.height(23.dp).fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.width(250.dp)) {
                    Row {
                        Checkbox(
                            checkedState,
                            onCheckedChange = { checkedState = it }
                        )
                        BaseText(
                            "Запомнить меня", fontSize = 12.sp,
                            color = Color(161, 161, 161),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Забыли пароль?",
                        color = Color(127, 38, 91),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() })
                }
            }

            VerticalSpace(20.dp)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (!StringChecker.checkMailString(emailText)) {
                            showDialogFun("Введите email в формате *@mgutu.loc")
                            return@Button
                        }
                        if (!StringChecker.checkPassword(firstPassText)) {
                            showDialogFun("Пароль должен иметь больше 8 символов")
                            return@Button
                        }
                        registrationViewModel.singIn(
                            emailText,
                            firstPassText,
                            onSuccess = {
                                if (checkedState) {
                                    settingsViewModel.setEmail(emailText)
                                    settingsViewModel.setPassword(firstPassText)
                                }
                                onNavigateToAuthenticatedRoute(emailText, firstPassText)
                                firstPassText = ""
                                emailText = ""
                            }
                        )
                    },
                    content = {
                        BaseText(
                            "Вход",
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
                        "Ещё не зарегестрированы?",
                        fontSize = 12.sp,
                        color = Color(130, 130, 130)
                    )
                    HorizontalSpace(4.dp)
                    Text(
                        "Создать аккаунт",
                        color = Color(127, 38, 91),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigateToRegistration() })
                }
            }

        }
    }
}