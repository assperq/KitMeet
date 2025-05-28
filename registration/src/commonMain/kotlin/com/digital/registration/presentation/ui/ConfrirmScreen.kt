package com.digital.registration.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConfirmScreen(
    email: String,
    password : String,
    onVerified: () -> Unit,
    onResendClick: () -> Unit = {},
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isVerified by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Подтверждение", color = Color.White)
                },
                actions = {
                    IconButton(onClick = { onSignOut() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Выйти",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Мы отправили письмо с подтверждением на:",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(email, fontWeight = FontWeight.Medium, color = Color(0xFF7F265B))

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF7F265B))
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            error = null
                            try {
                                SupabaseManager.supabaseClient.auth.signInWith(Email) {
                                    this.email = email
                                    this.password = password
                                }
                                val user = SupabaseManager.supabaseClient.auth.currentUserOrNull()
                                if (user?.emailConfirmedAt != null) {
                                    onVerified()
                                } else {
                                    error = "Email еще не подтвержден. Проверьте почту."
                                }
                            } catch (e: Throwable) {
                                error = "Email не подтвержден или пароль неверный"
                            }
                            isLoading = false
                        }
                    }
                ) {
                    Text("Проверить статус")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(text = error!!, color = Color.Red, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onResendClick) {
                Text("Отправить письмо ещё раз")
            }
        }
    }
}

