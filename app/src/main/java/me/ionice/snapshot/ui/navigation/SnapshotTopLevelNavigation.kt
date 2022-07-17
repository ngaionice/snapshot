package me.ionice.snapshot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import me.ionice.snapshot.ui.days.DAY_ROUTE
import me.ionice.snapshot.ui.navigation.graph.METRIC_ROUTE
import me.ionice.snapshot.ui.settings.SETTINGS_ROUTE

class SnapshotTopLevelNavigation(private val navController: NavHostController) {

    fun navigateTo(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

data class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val name: String
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = DAY_ROUTE,
        selectedIcon = Icons.Filled.Today,
        unselectedIcon = Icons.Outlined.Today,
        name = "Days"
    ),
    TopLevelDestination(
        route = METRIC_ROUTE,
        selectedIcon = Icons.Filled.DataUsage,
        unselectedIcon = Icons.Outlined.DataUsage,
        name = "Metrics"
    ),
    TopLevelDestination(
        route = SETTINGS_ROUTE,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        name = "Settings"
    )
)