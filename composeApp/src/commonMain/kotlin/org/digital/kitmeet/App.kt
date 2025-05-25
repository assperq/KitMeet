package org.digital.kitmeet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.ui.ConversationScreen
import com.digital.registration.presentation.navigation.RegistrationRoutes
import com.digital.registration.presentation.provideRegistrationViewModel
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.digital.settings.presentation.SettingsScreen
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.CardsScreen
import com.example.cardss.CardsViewModel
import com.example.cardss.SwipeTracker
import com.example.profile.presentation.EditProfileScreen
import com.example.profile.presentation.ProfileScreen
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.ProfileViewModelFactory
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.digital.kitmeet.notifications.BaseFcmHandler
import org.digital.kitmeet.notifications.FCMTokenProvider
import org.digital.kitmeet.notifications.FcmDelegate
import org.digital.kitmeet.notifications.FcmDelegate.handler
import org.digital.kitmeet.notifications.NotificationService
import org.digital.kitmeet.notifications.ServiceLocator

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
        val userId = session?.user?.id.orEmpty()

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination?.route

        val chatViewModel = ChatViewModel()
        ServiceLocator.initialize(chatViewModel)
        val fcmTokenProvider = FCMTokenProvider.getInstance()
        handler = BaseFcmHandler(NotificationService.getInstance(), chatViewModel)

        val showBottomBar = when (currentDestination) {
            MainRoutes.profile -> true                // Показываем всегда на профиле
            "profile_edit" -> false                   // Не показываем на редактировании профиля
            MainRoutes.cards, MainRoutes.chat -> true
            else -> false
        }

        val swipeTracker = remember {
            SwipeTracker(Settings())
        }

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
                                fcmTokenProvider.initialize()
                            }
                        )
                    }

                    composable(RegistrationRoutes.registrationRoute) {
                        RegistrationScreen(
                            onNavigateToLogin = { navController.popBackStack() },
                            onNavigateToAuthenticatedRoute = {
                                // После регистрации навигируем на профиль редактирования
                                navController.navigate("profile_edit") {
                                    popUpTo("auth") { inclusive = true }
                                }
                                fcmTokenProvider.initialize()
                            }
                        )
                    }
                }

                // Основные экраны
                composable(MainRoutes.profile) {
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
                                CircularProgressIndicator(color = colorScheme.primary)
                            }
                        }

                        isComplete && profile != null -> {
                            ProfileScreen(profile = profile!!, viewModel = viewModel)
                        }

                        else -> {
                            // Если профиль не полон, то вместо этого должен быть экран редактирования
                            // Но с твоей логикой редирект должен быть сделан раньше
                            // Можно тут или сделать навигацию на "profile_edit"
                            LaunchedEffect(Unit) {
                                navController.navigate("profile_edit") {
                                    popUpTo(MainRoutes.profile) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                composable("profile_edit") {
                    val viewModel: ProfileViewModel = viewModel(
                        factory = ProfileViewModelFactory(supabaseClient),
                        key = "ProfileViewModel_$userId"
                    )

                    EditProfileScreen(
                        userId = userId,
                        onSave = { id, name, prof, group, mainPhoto, galleryPhotos, lookingFor, aboutMe,
                                   gender, age, status, specialty ->

                            // Запускаем корутину для сохранения профиля
                            viewModel.viewModelScope.launch {
                                val success = viewModel.saveProfile(
                                    id, name, prof, group,
                                    mainPhoto, galleryPhotos,
                                    lookingFor, aboutMe,
                                    gender, age, status, specialty
                                )
                                if (success) {
                                    navController.navigate(MainRoutes.profile) {
                                        popUpTo("profile_edit") { inclusive = true }
                                    }
                                }
                            }
                        }
                    )
                }

                // Остальные экраны остаются без изменений
                composable(
                    route = "${MainRoutes.profileDetails}/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val otherUserId = backStackEntry.arguments?.getString("userId").orEmpty()
                    val otherProfileViewModel: ProfileViewModel = viewModel(
                        factory = ProfileViewModelFactory(supabaseClient),
                        key = "ProfileViewModel_$otherUserId"
                    )

                    val isLoading by otherProfileViewModel.isLoading.collectAsState()
                    val profile by otherProfileViewModel.currentProfile.collectAsState()
                    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(supabaseClient))

                    LaunchedEffect(otherUserId) {
                        otherProfileViewModel.loadProfile(otherUserId)
                    }

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = colorScheme.primary)
                            }
                        }

                        profile != null -> {
                            ProfileScreen(
                                profile = profile!!,
                                showBackButton = true,
                                onBackClick = { navController.popBackStack() },
                                viewModel = viewModel
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Профиль не найден")
                            }
                        }
                    }
                }

                composable(MainRoutes.cards) {
                    CardsScreen(
                        swipeTracker = swipeTracker,
                        onProfileClick = { clickedUserId ->
                            navController.navigate("${MainRoutes.profileDetails}/$clickedUserId")
                        }
                    )
                }

                composable(MainRoutes.chat) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        chatViewModel.loadConversations()
                        val cardsViewModel = remember { CardsViewModel(SupabaseManager.supabaseClient,
                            swipeTracker = swipeTracker) }
                        ConversationScreen(navController, cardsViewModel, chatViewModel)
                    }
                }
            }
        }
    }
}