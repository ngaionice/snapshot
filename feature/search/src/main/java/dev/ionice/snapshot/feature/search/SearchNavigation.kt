package dev.ionice.snapshot.feature.search

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.navigation.SEARCH_ROUTE
import dev.ionice.snapshot.core.navigation.SearchDestination
import dev.ionice.snapshot.core.ui.animationDurationMs

fun NavGraphBuilder.searchGraph(navController: NavHostController) {
    navigation(
        route = SEARCH_ROUTE,
        startDestination = "${SearchDestination.route}/${SearchDestination.destination}"
    ) {
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${SearchDestination.route}/${SearchDestination.destination}",
            enterTransition = { fadeIn(tween(animationDurationMs)) },
            exitTransition = { fadeOut(tween(animationDurationMs)) },
            popEnterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = { fadeOut(tween(animationDurationMs)) }
        ) {
            SearchRoute(navigator = navigator)
        }
    }
}
