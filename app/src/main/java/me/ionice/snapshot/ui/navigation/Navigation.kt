package me.ionice.snapshot.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(navController: NavHostController, options: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        options.forEach {
            screen -> NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
            onClick = {
                navController.navigate(screen.name) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {Icon(screen.icon, contentDescription = screen.name)},
            label = {Text(screen.name)},
            colors = NavigationBarItemDefaults.colors())
        }
    }
}