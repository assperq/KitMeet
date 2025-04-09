package com.digital.registration.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.digital.registration.presentation.ui.LoginScreen
import com.digital.registration.presentation.ui.RegistrationScreen

@Composable
fun NavigationRegistration(
    navController: NavHostController,
    mainRoute : String = ""
) {
    val navigateToMain = { navController.navigate(mainRoute) }

    NavHost(navController = navController, startDestination = RegistrationRoutes.loginRoute) {
        composable(RegistrationRoutes.loginRoute) {
            LoginScreen(
                onNavigateToRegistration = { navController.navigate(RegistrationRoutes.registrationRoute) }
            )
        }
        composable(RegistrationRoutes.registrationRoute) {
            RegistrationScreen(
                onNavigateToLogin = { navController.navigate(RegistrationRoutes.loginRoute) }
            )
        }
    }
}