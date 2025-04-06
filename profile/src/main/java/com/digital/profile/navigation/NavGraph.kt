package com.digital.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.digital.profile.presentation.ProfileScreen

fun NavGraphBuilder.profileNavigation(navController: NavController) {
    navigation(
        startDestination = "profile",
        route = "profile_route"
    ) {
        composable(route = "profile") {
            ProfileScreen()
        }
    }
}