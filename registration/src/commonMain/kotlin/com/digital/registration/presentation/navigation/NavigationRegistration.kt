package com.digital.registration.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen
import com.example.profile.presentation.ProfileScreen
import com.example.profile.presentation.ProfileSetupScreen

@Composable
fun NavigationRegistration(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = RegistrationRoutes.loginRoute
    ) {
        composable(RegistrationRoutes.loginRoute) {
            LoginScreen(
                onNavigateToAuthenticatedRoute = {
                    navController.navigate(RegistrationRoutes.profileSetupRoute)
                }
            )
        }
        composable(RegistrationRoutes.registrationRoute) {
            RegistrationScreen(
                onNavigateToAuthenticatedRoute = {
                    navController.navigate(RegistrationRoutes.profileSetupRoute)
                }
            )
        }
        composable(RegistrationRoutes.profileSetupRoute) {
            ProfileSetupScreen {
                navController.navigate(RegistrationRoutes.mainProfileRoute)
            }
        }

        composable(RegistrationRoutes.mainProfileRoute) {
            ProfileScreen()
        }
    }
}