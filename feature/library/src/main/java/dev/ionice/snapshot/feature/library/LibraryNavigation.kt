package dev.ionice.snapshot.feature.library

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ionice.snapshot.core.navigation.LIBRARY_ROUTE
import dev.ionice.snapshot.core.navigation.LibraryHomeDestination
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.ui.animationDurationMs

fun NavGraphBuilder.libraryGraph(navController: NavHostController) {
    navigation(route = LIBRARY_ROUTE,
        startDestination = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}"
    ) {
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}",
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
            LibraryRoute(navigator = navigator)
        }
    }
}