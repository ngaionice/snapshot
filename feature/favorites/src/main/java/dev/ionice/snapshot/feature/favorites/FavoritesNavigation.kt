package dev.ionice.snapshot.feature.favorites

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ionice.snapshot.core.navigation.FavoritesDestination
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.ui.animationDurationMs

fun NavGraphBuilder.favoritesGraph(navController: NavHostController) {
    navigation(
        route = FavoritesDestination.route,
        startDestination = "${FavoritesDestination.route}/${FavoritesDestination.destination}"
    ) {
        val navigator = NavigatorImpl(navController)
        composable(route = "${FavoritesDestination.route}/${FavoritesDestination.destination}",
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
            FavoritesRoute(navigator = navigator)
        }
    }
}