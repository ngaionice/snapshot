package dev.ionice.snapshot.ui.library

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.ionice.snapshot.core.navigation.LIBRARY_ROUTE
import dev.ionice.snapshot.core.navigation.LibraryHomeDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.core.navigation.NavigatorImpl

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.libraryGraph(navController: NavHostController) {
    navigation(route = LIBRARY_ROUTE,
        startDestination = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}",
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
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}",
            enterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = { fadeOut(tween(animationDurationMs)) }) {
            LibraryRoute(navigator = navigator)
        }
    }
}