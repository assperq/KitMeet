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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.digital.chat.data.ChatRepositoryImpl
import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.ui.ConversationScreen
import com.digital.registration.presentation.navigation.RegistrationRoutes
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.digital.settings.presentation.SettingsScreen
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.presentation.CardsScreens.CardsScreen
import com.example.cardss.CardsViewModel
import com.example.cardss.data.CardsRepository
import com.example.cardss.domain.CardsRepositoryImpl
import com.example.cardss.presentation.CardsScreens.MatchScreen
import com.example.cardss.presentation.SwipeTracker
import com.example.profile.data.ProfileRepositoryImpl
import com.example.profile.di.ProfileViewModelFactory
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.editProfileScreens.EditProfileScreen
import com.example.profile.presentation.profileScreens.ProfileScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import org.digital.kitmeet.notifications.BaseFcmHandler
import org.digital.kitmeet.notifications.FCMTokenProvider
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

        val chatViewModel = remember { ChatViewModel() }
        ServiceLocator.initialize(chatViewModel)
        val fcmTokenProvider = FCMTokenProvider.getInstance()
        handler = BaseFcmHandler(NotificationService.getInstance(), chatViewModel)

        var showBottomBar = when (currentDestination) {
            MainRoutes.profile -> true
            "profile_edit" -> false
            MainRoutes.selectedChat -> true
            MainRoutes.cards, MainRoutes.chat -> true
            else -> false
        }

        val swipeTracker = remember { SwipeTracker(Settings()) }
        val cardsRepository = remember { CardsRepositoryImpl(SupabaseManager.supabaseClient) }

        // Создаем CardsViewModel с помощью remember
        val cardsViewModel = remember { CardsViewModel(cardsRepository, swipeTracker) }

        // Отслеживаем состояние матча
        val matchProfile by cardsViewModel.matchFound.collectAsState()

        val profileRepository = remember {
            ProfileRepositoryImpl(supabaseClient)
        }
        val chatRepository = remember {
            ChatRepositoryImpl() // если нужно, добавьте зависимость от supabaseClient
        }


        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = "auth",
                    modifier = Modifier.fillMaxSize()
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
                                    navController.navigate("profile_edit") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                    fcmTokenProvider.initialize()
                                }
                            )
                        }
                    }

                    composable(MainRoutes.profile) {
                        // Создаем ProfileViewModel с помощью remember
                        val viewModel = remember {
                            ProfileViewModel(profileRepository)
                        }

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
                                ProfileScreen(profile = profile!!, viewModel = viewModel, navController = navController)
                            }

                            else -> {
                                LaunchedEffect(Unit) {
                                    navController.navigate("profile_edit") {
                                        popUpTo(MainRoutes.profile) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    composable("settings") {
                        SettingsScreen(navController)
                    }

                    composable("profile_edit") {
                        val viewModel = remember {
                            ProfileViewModel(profileRepository)
                        }

                        EditProfileScreen(
                            userId = userId,
                            onSave = { id, name, prof, group, mainPhoto, galleryPhotos, lookingFor, aboutMe,
                                       gender, age, status, specialty ->

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

                    composable(
                        route = "${MainRoutes.profileDetails}/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val otherUserId = backStackEntry.arguments?.getString("userId").orEmpty()

                        val otherProfileViewModel = remember {
                            ProfileViewModel(profileRepository)
                        }

                        val isLoading by otherProfileViewModel.isLoading.collectAsState()
                        val profile by otherProfileViewModel.currentProfile.collectAsState()
                        val currentUserProfileViewModel = remember {
                            ProfileViewModel(profileRepository)
                        }

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
                                    viewModel = currentUserProfileViewModel,
                                    navController = navController
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
                            viewModel = cardsViewModel,
                            swipeTracker = swipeTracker,
                            onProfileClick = { clickedUserId ->
                                navController.navigate("${MainRoutes.profileDetails}/$clickedUserId")
                            }
                        )
                    }

                    composable(MainRoutes.chat) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            chatViewModel.loadConversations()
                            ConversationScreen(navController, cardsViewModel, chatViewModel)
                        }
                    }

                    composable(MainRoutes.selectedChat + "/{userId}") { backStackEntry ->
                        showBottomBar = true
                        val otherUserId = backStackEntry.arguments?.getString("userId").orEmpty()
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            chatViewModel.loadConversations()
                            ConversationScreen(navController, cardsViewModel, chatViewModel, selectedChat = otherUserId)
                        }
                    }
                }

                if (matchProfile != null) {
                    val currentUserId = supabaseClient.auth.currentUserOrNull()?.id.orEmpty()

                    // Создаем временную ViewModel для профиля текущего пользователя
                    val currentUserProfileViewModel = remember {
                        ProfileViewModel(profileRepository)
                    }

                    LaunchedEffect(currentUserId) {
                        if (currentUserId.isNotEmpty()) {
                            currentUserProfileViewModel.loadProfile(currentUserId)
                        }
                    }

                    val currentUserProfile by currentUserProfileViewModel.currentProfile.collectAsState()

                    MatchScreen(
                        currentUserProfile = currentUserProfile,
                        matchedProfile = matchProfile,
                        onSayHi = {
                            chatViewModel.createConversationIfNeeded(
                                user1Id = currentUserId,
                                user2Id = matchProfile?.user_id ?: ""
                            )
                            cardsViewModel.clearMatch()
                            navController.navigate("${MainRoutes.selectedChat}/${matchProfile?.user_id}")
                        },
                        onKeepSwiping = {
                            cardsViewModel.clearMatch()
                        }
                    )
                }
            }
        }
    }
}