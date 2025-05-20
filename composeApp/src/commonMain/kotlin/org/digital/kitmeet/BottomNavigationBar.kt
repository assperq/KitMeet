package org.digital.kitmeet

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem("Карты", MainRoutes.cards, Icons.Default.Favorite ),
        NavItem("Чаты", MainRoutes.chat, Icons.AutoMirrored.Filled.Message),
        NavItem("Форум", MainRoutes.obs, Icons.Default.QuestionAnswer ),
        NavItem("Профиль", MainRoutes.profile, Icons.Default.Person),
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF7F265B) else Color.Gray // 🔴 Цвет иконки
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (currentRoute == item.route) Color(0xFF7F265B) else Color.Gray // 🔴 Цвет текста
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(MainRoutes.profile) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                selectedContentColor = Color(0xFF7F265B), // 🔴 Цвет при выделении
                unselectedContentColor = Color.Gray      // 🔘 Цвет при отсутствии выбора
            )
        }
    }
}

data class NavItem(val label: String, val route: String, val icon: ImageVector)

object MainRoutes {
    const val profile = "profile"
    const val profileDetails = "profileDetails"
    const val cards = "cards"
    const val chat = "chat"
    const val obs = "obs"
}

object ProfileRoutes {
    const val view = "profile/view"
    const val edit = "profile/edit"
}