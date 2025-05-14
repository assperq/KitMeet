package org.digital.kitmeet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.digital.registration.presentation.navigation.RegistrationRoutes
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.CardsScreen
import com.example.profile.presentation.EditProfileScreen
import com.example.profile.presentation.ProfileScreen
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.ProfileViewModelFactory
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
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

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val showBottomBar = currentDestination?.route in listOf(
            MainRoutes.cards,
            MainRoutes.chat,
            MainRoutes.profile
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "auth",
                modifier = Modifier.padding(innerPadding)
            ) {
                // Экраны аутентификации
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
                                navController.navigate(MainRoutes.profile) {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(RegistrationRoutes.registrationRoute) {
                        RegistrationScreen(
                            onNavigateToLogin = { navController.popBackStack() },
                            onNavigateToAuthenticatedRoute = {
                                navController.navigate(MainRoutes.profile) {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }
                }

                // Основные экраны
                composable(MainRoutes.profile) {
                    val session = supabaseClient.auth.currentSessionOrNull()
                    val userId = session?.user?.id ?: ""

                    val viewModel: ProfileViewModel = viewModel(
                        factory = ProfileViewModelFactory(supabaseClient),
                        key = "ProfileViewModel_$userId"
                    )

                    val isLoading by viewModel.isLoading.collectAsState()
                    val isComplete by viewModel.isProfileCompleteFlow.collectAsState()
                    val profile by viewModel.currentProfile.collectAsState()

                    LaunchedEffect(userId) {
                        viewModel.loadProfile(userId)
                    }

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF7F265B))
                            }
                        }

                        isComplete && profile != null -> {
                            ProfileScreen(profile = profile!!)
                        }

                        else -> {
                            EditProfileScreen(
                                userId = userId,
                                onSave = { id, name, prof, group, mainPhoto, galleryPhotos, lookingFor, aboutMe ->
                                    viewModel.viewModelScope.launch {
                                        val success = viewModel.saveProfile(
                                            id, name, prof, group,
                                            mainPhoto, galleryPhotos, lookingFor, aboutMe
                                        )
                                        println("🔥 Сохранили профиль: $success")
                                    }
                                }
                            )
                        }
                    }
                }

                // Пустые заглушки для Cards и Chat
                composable(MainRoutes.cards) {
                    CardsScreen()
                }

                composable(MainRoutes.chat) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Чат")
                    }
                }
            }
        }
    }
}