package me.ionice.snapshot.ui.settings

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.ui.common.animationDurationMs
import me.ionice.snapshot.ui.navigation.NavigationDestination
import me.ionice.snapshot.ui.navigation.parentViewModel
import me.ionice.snapshot.ui.settings.screens.BackupRoute
import me.ionice.snapshot.ui.settings.screens.HomeRoute
import me.ionice.snapshot.ui.settings.screens.NotificationsRoute
import me.ionice.snapshot.ui.settings.screens.ThemingRoute

const val SETTINGS_ROUTE = "settings"

object SettingsHomeDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "home"
}

object SettingsBackupDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "backup"
}

object SettingsNotificationsDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "notifications"
}

object SettingsThemingDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "theming"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    networkRepository: NetworkRepository,
    preferencesRepository: PreferencesRepository
) {
    navigation(
        route = SETTINGS_ROUTE,
        startDestination = "${SettingsHomeDestination.route}/${SettingsHomeDestination.destination}",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left, tween(
                    animationDurationMs
                )
            )
        },
        exitTransition = { fadeOut(tween(animationDurationMs)) },
        popEnterTransition = { fadeIn(tween(animationDurationMs)) },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right, tween(
                    animationDurationMs
                )
            )
        }
    ) {
        composable(route = "${SettingsHomeDestination.route}/${SettingsHomeDestination.destination}") {
            HomeRoute(
                onNavigateToBackup = { navController.navigate("${SettingsBackupDestination.route}/${SettingsBackupDestination.destination}") },
                onNavigateToNotifications = { navController.navigate("${SettingsNotificationsDestination.route}/${SettingsNotificationsDestination.destination}") },
                onNavigateToTheming = { navController.navigate("${SettingsThemingDestination.route}/${SettingsThemingDestination.destination}") }
            )
        }
        composable(route = "${SettingsBackupDestination.route}/${SettingsBackupDestination.destination}") {
            BackupRoute(
                viewModel = it.parentViewModel(
                    navController = navController,
                    factory = SettingsViewModel.provideFactory(
                        networkRepository,
                        preferencesRepository
                    )
                ),
                onBack = navController::popBackStack
            )
        }
        composable(route = "${SettingsNotificationsDestination.route}/${SettingsNotificationsDestination.destination}") {
            NotificationsRoute(
                viewModel = it.parentViewModel(
                    navController = navController,
                    factory = SettingsViewModel.provideFactory(
                        networkRepository,
                        preferencesRepository
                    )
                ),
                onBack = navController::popBackStack
            )
        }
        composable(route = "${SettingsThemingDestination.route}/${SettingsThemingDestination.destination}") {
            ThemingRoute(
                viewModel = it.parentViewModel(
                    navController = navController,
                    factory = SettingsViewModel.provideFactory(
                        networkRepository,
                        preferencesRepository
                    )
                ),
                onBack = navController::popBackStack
            )
        }
    }
}