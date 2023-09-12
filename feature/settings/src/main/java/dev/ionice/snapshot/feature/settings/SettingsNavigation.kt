package dev.ionice.snapshot.feature.settings

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ionice.snapshot.core.navigation.SETTINGS_ROUTE
import dev.ionice.snapshot.core.navigation.SettingsBackupDestination
import dev.ionice.snapshot.core.navigation.SettingsHomeDestination
import dev.ionice.snapshot.core.navigation.SettingsNotificationsDestination
import dev.ionice.snapshot.core.navigation.SettingsThemingDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.feature.settings.screens.BackupRoute
import dev.ionice.snapshot.feature.settings.screens.HomeRoute
import dev.ionice.snapshot.feature.settings.screens.NotificationsRoute
import dev.ionice.snapshot.feature.settings.screens.ThemingRoute

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        route = SETTINGS_ROUTE,
        startDestination = "${SettingsHomeDestination.route}/${SettingsHomeDestination.destination}"
    ) {
        composable(route = "${SettingsHomeDestination.route}/${SettingsHomeDestination.destination}",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(
                        animationDurationMs
                    )
                )
            },
            exitTransition = { fadeOut(tween(animationDurationMs)) },
            popEnterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(
                        animationDurationMs
                    )
                )
            }) {
            HomeRoute(
                onNavigateToBackup = { navController.navigate("${SettingsBackupDestination.route}/${SettingsBackupDestination.destination}") },
                onNavigateToNotifications = { navController.navigate("${SettingsNotificationsDestination.route}/${SettingsNotificationsDestination.destination}") },
                onNavigateToTheming = { navController.navigate("${SettingsThemingDestination.route}/${SettingsThemingDestination.destination}") },
                onBack = navController::popBackStack
            )
        }
        composable(route = "${SettingsBackupDestination.route}/${SettingsBackupDestination.destination}",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(
                        animationDurationMs
                    )
                )
            },
            exitTransition = { fadeOut(tween(animationDurationMs)) },
            popEnterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(
                        animationDurationMs
                    )
                )
            }) {
            BackupRoute(onBack = navController::popBackStack)
        }
        composable(route = "${SettingsNotificationsDestination.route}/${SettingsNotificationsDestination.destination}",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(
                        animationDurationMs
                    )
                )
            },
            exitTransition = { fadeOut(tween(animationDurationMs)) },
            popEnterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(
                        animationDurationMs
                    )
                )
            }) {
            NotificationsRoute(onBack = navController::popBackStack)
        }
        composable(route = "${SettingsThemingDestination.route}/${SettingsThemingDestination.destination}",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(
                        animationDurationMs
                    )
                )
            },
            exitTransition = { fadeOut(tween(animationDurationMs)) },
            popEnterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(
                        animationDurationMs
                    )
                )
            }) {
            ThemingRoute(onBack = navController::popBackStack)
        }
    }
}