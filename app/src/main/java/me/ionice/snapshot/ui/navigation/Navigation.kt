package me.ionice.snapshot.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.days.DaysScreen
import me.ionice.snapshot.ui.days.DaysViewModel
import me.ionice.snapshot.ui.metrics.MetricsScreen
import me.ionice.snapshot.ui.metrics.MetricsViewModel
import me.ionice.snapshot.ui.settings.SettingsScreen
import me.ionice.snapshot.ui.settings.SettingsViewModel

val navOptions = listOf(
    Screen.Days,
    Screen.Metrics,
    Screen.Settings
)

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    toggleBottomNav: (Boolean) -> Unit
) {

    NavHost(navController = navController, startDestination = Screen.Days.name) {
        composable(Screen.Days.name) {
            DaysScreen(viewModel = viewModel(
                factory = DaysViewModel.provideFactory(
                    appContainer.dayRepository,
                    appContainer.metricRepository
                )
            ), showBottomNav = toggleBottomNav)
        }

        composable(Screen.Metrics.name) {
            MetricsScreen(viewModel = viewModel(factory = MetricsViewModel.provideFactory(appContainer.metricRepository)), toggleBottomNav = toggleBottomNav)
        }

        composable(Screen.Settings.name) {
            SettingsScreen(viewModel =
            viewModel(factory = SettingsViewModel.provideFactory(appContainer.networkRepository, appContainer.preferencesRepository)), toggleBottomNav = toggleBottomNav)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        navOptions.forEach { screen ->
            NavigationBarItem(
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
                icon = { Icon(screen.icon, contentDescription = screen.name) },
                label = { Text(screen.name) },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}