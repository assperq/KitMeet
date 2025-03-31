package com.digital.registration.presintation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kitmeet.registration.generated.resources.Res
import kitmeet.registration.generated.resources.ic_invisible_password
import kitmeet.registration.generated.resources.ic_ukit_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegistrationScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToAuthenticatedRoute: () -> Unit = {}
) {

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

    Container {
        Column {
            Image(painterResource(Res.drawable.ic_ukit_logo), null)
            VerticalSpace()
            BaseText("Регистрация аккаунта", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            VerticalSpace(2.dp)
            BaseText("Готовы начать оттерабанивать?", color = Color(82,82,82), fontSize = 16.sp)
            VerticalSpace()
            val topTextFieldColor = Color(130,130,130)
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

            Row(
                modifier = Modifier.height(23.dp).fillMaxWidth()
            ) {
                var checkedState by remember {
                    mutableStateOf(false)
                }
                Checkbox(
                    checkedState,
                    onCheckedChange = {checkedState = it}
                )
                BaseText("Я согласен с правилами и политикой компании", fontSize = 12.sp,
                    color = Color(161, 161, 161),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxSize())
            }

            VerticalSpace(20.dp)

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Button(
                    onClick = onNavigateToAuthenticatedRoute,
                    content = {
                        BaseText("Зарегистрироваться",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
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