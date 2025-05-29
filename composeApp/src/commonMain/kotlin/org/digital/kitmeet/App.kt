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
import com.digital.registration.presentation.RegistrationViewModel
import com.digital.registration.data.UserRemoteDatasourceImpl
import com.digital.registration.data.UserRepositoryImpl
import com.digital.registration.presentation.navigation.RegistrationRoutes
import com.digital.registration.presentation.provideRegistrationViewModel
import com.digital.registration.presentation.ui.ConfirmScreen
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.digital.settings.presentation.SettingsScreen
import com.digital.settings.presentation.SettingsViewModel
import com.digital.settings.presentation.provideSettingsViewModel
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.presentation.CardsScreens.CardsScreen
import com.example.cardss.CardsViewModel
import com.example.cardss.domain.CardsRepositoryImpl
import com.example.cardss.presentation.CardsScreens.MatchScreen
import com.example.cardss.presentation.SwipeTracker
import com.example.profile.data.ProfileRepositoryImpl
import com.example.profile.di.ProfileViewModelFactory
import com.example.profile.presentation.editProfileScreens.EditProfileScreen
import com.example.profile.presentation.profileScreens.ProfileScreen
import com.example.profile.presentation.ProfileViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import org.digital.kitmeet.MainRoutes.selectedChat
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

        val settingsViewModel = SettingsViewModel.getViewModel()
        val registrationViewModel = remember {
            RegistrationViewModel(UserRepositoryImpl(UserRemoteDatasourceImpl()))
        }

        val checkForConfirm = { email: String, password: String, work: () -> Unit ->
            if (supabaseClient.auth.currentUserOrNull()?.emailConfirmedAt == null) {
                navController.navigate("${MainRoutes.unconfirmed}/$email/$password")
            } else {
                work()
            }
        }

        val chatViewModel = remember { ChatViewModel() }
        ServiceLocator.initialize(chatViewModel)
        val fcmTokenProvider = FCMTokenProvider.getInstance()
        handler = BaseFcmHandler(NotificationService.getInstance(), chatViewModel, settingsViewModel)

        var startDestination by remember { mutableStateOf(MainRoutes.splash) }

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination?.route

        LaunchedEffect(settingsViewModel) {
            settingsViewModel.setting
                .filterNotNull()
                .distinctUntilChanged { old, new ->
                    old.password == new.password && old.email == new.email
                }
                .collect { settings ->
                    if (settings.email.isNotEmpty() && settings.password.isNotEmpty()) {
                        registrationViewModel.signIn(settings.email, settings.password) {
                            fcmTokenProvider.initialize()
                            checkForConfirm(settings.email, settings.password) {
                                startDestination = MainRoutes.profile
                                navController.navigate(MainRoutes.profile) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    } else {
                        startDestination = "auth"
                    }
                }
        }

        var showBottomBar = when (currentDestination) {
            MainRoutes.profile -> true
            "profile_edit" -> false
            MainRoutes.selectedChat -> true
            MainRoutes.cards, MainRoutes.chat, MainRoutes.obs -> true
            else -> false
        }

        val swipeTracker = remember { SwipeTracker(Settings()) }
        val cardsRepository = remember { CardsRepositoryImpl(SupabaseManager.supabaseClient) }
        val cardsViewModel = remember { CardsViewModel(cardsRepository, swipeTracker) }
        val matchProfile by cardsViewModel.matchFound.collectAsState()

        val profileRepository = remember { ProfileRepositoryImpl(supabaseClient) }

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
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(MainRoutes.splash) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    composable("${MainRoutes.unconfirmed}/{email}/{password}") { backStack ->
                        val email = backStack.arguments?.getString("email").orEmpty()
                        val password = backStack.arguments?.getString("password").orEmpty()
                        ConfirmScreen(
                            email = email,
                            password = password,
                            onVerified = {
                                navController.navigate(MainRoutes.profile) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onResendClick = {},
                            onSignOut = {
                                settingsViewModel.signOut()
                                navController.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    navigation(
                        startDestination = RegistrationRoutes.loginRoute,
                        route = "auth"
                    ) {
                        composable(RegistrationRoutes.loginRoute) {
                            LoginScreen(
                                settingsViewModel = settingsViewModel,
                                onNavigateToRegistration = {
                                    navController.navigate(RegistrationRoutes.registrationRoute)
                                },
                                onNavigateToAuthenticatedRoute = { email, password ->
                                    checkForConfirm(email, password) {
                                        navController.navigate(MainRoutes.profile) {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                        fcmTokenProvider.initialize()
                                    }
                                }
                            )
                        }

                        composable(RegistrationRoutes.registrationRoute) {
                            RegistrationScreen(
                                settingsViewModel = settingsViewModel,
                                onNavigateToLogin = { navController.popBackStack() },
                                onNavigateToAuthenticatedRoute = { email, password ->
                                    checkForConfirm(email, password) {
                                        navController.navigate("profile_edit") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                        fcmTokenProvider.initialize()
                                    }
                                }
                            )
                        }
                    }

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
                                ProfileScreen(
                                    profile = profile!!,
                                    viewModel = viewModel,
                                    navController = navController
                                )
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

                    composable(MainRoutes.settings) {
                        SettingsScreen(navController = navController)
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
                        val otherProfileViewModel: ProfileViewModel = viewModel(
                            factory = ProfileViewModelFactory(supabaseClient),
                            key = "ProfileViewModel_$otherUserId"
                        )

                        val currentUserProfileViewModel: ProfileViewModel = viewModel(
                            factory = ProfileViewModelFactory(supabaseClient),
                            key = "ProfileViewModel_$userId"
                        )

                        val isLoading by otherProfileViewModel.isLoading.collectAsState()
                        val profile by otherProfileViewModel.currentProfile.collectAsState()

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

                    composable("${MainRoutes.selectedChat}/{userId}") { backStackEntry ->
                        showBottomBar = true
                        val otherUserId = backStackEntry.arguments?.getString("userId").orEmpty()
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            chatViewModel.loadConversations()
                            ConversationScreen(
                                navController = navController,
                                cardsViewModel = cardsViewModel,
                                chatViewModel = chatViewModel,
                                selectedChat = otherUserId
                            )
                        }
                    }

                    composable(MainRoutes.obs) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("СКОРО...")
                        }
                    }
                }

                if (matchProfile != null) {
                    val currentUserId = supabaseClient.auth.currentUserOrNull()?.id.orEmpty()
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