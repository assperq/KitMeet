package org.digital.kitmeet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.digital.registration.presentation.navigation.RegistrationRoutes
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.presentation.EditProfileScreen
import com.example.profile.presentation.ProfileScreen
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.ProfileViewModelFactory
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = Colors(
            primary = Color(127, 38, 91),
            primaryVariant = MaterialTheme.colors.primaryVariant,
            secondary = Color(127, 38, 91),
            secondaryVariant = MaterialTheme.colors.secondaryVariant,
            background = MaterialTheme.colors.background,
            surface = MaterialTheme.colors.surface,
            error = MaterialTheme.colors.error,
            onPrimary = MaterialTheme.colors.onPrimary,
            onSecondary = MaterialTheme.colors.onSecondary,
            onBackground = MaterialTheme.colors.onBackground,
            onSurface = MaterialTheme.colors.onSurface,
            onError = MaterialTheme.colors.onError,
            isLight = true
        )
    ) {
        val navController = rememberNavController()
        val supabaseClient = remember { SupabaseManager.supabaseClient }
        val session = supabaseClient.auth.currentSessionOrNull()
        val userId = session?.user?.id ?: ""
        val viewModel: ProfileViewModel = viewModel(
            factory = ProfileViewModelFactory(supabaseClient),
            key = "ProfileViewModel_$userId"
        )

        NavHost(
            navController = navController,
            startDestination = "auth" // Указываем корневой маршрут
        ) {
            // Группируем экраны аутентификации
            navigation(
                startDestination = RegistrationRoutes.loginRoute,
                route = "auth"
            ) {
                composable(RegistrationRoutes.loginRoute) {
                    LoginScreen(
                        onNavigateToRegistration = {
                            navController.navigate(RegistrationRoutes.registrationRoute)
                        },
                        onNavigateToAuthenticatedRoute = {
                            navController.navigate("profile") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                composable(RegistrationRoutes.registrationRoute) {
                    RegistrationScreen(
                        onNavigateToLogin = { navController.popBackStack() },
                        onNavigateToAuthenticatedRoute = {
                            navController.navigate("profile") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }
            }

            // Основные экраны приложения
            composable("profile") {
                val session = supabaseClient.auth.currentSessionOrNull()
                val userId = session?.user?.id ?: ""

                // ✅ Создаём ViewModel здесь, с уникальным ключом по userId
                val viewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(supabaseClient),
                    key = "ProfileViewModel_$userId"
                )

                val isComplete by viewModel.isProfileCompleteFlow.collectAsState()
                val profile by viewModel.currentProfile.collectAsState()

                // ⬇️ Загружаем профиль
                LaunchedEffect(userId) {
                    viewModel.loadProfile(userId)
                }

                if (isComplete && profile != null) {
                    ProfileScreen(profile = profile!!)
                } else {
                    EditProfileScreen(
                        userId = userId,
                        onSave = { id, name, prof, group ->
                            viewModel.viewModelScope.launch {
                                val success = viewModel.saveProfile(id, name, prof, group)
                                println("🔥 Сохранили профиль: $success")
                            }
                        }
                    )
                }
            }
        }
    }
}