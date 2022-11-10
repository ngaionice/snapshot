package dev.ionice.snapshot.feature.search

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.ionice.snapshot.core.navigation.*
import dev.ionice.snapshot.core.ui.animationDurationMs

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.searchGraph(navController: NavHostController) {
    navigation(
        route = SEARCH_ROUTE,
        startDestination = "${SearchDestination.route}/${SearchDestination.destination}",
        enterTransition = { fadeIn(tween(animationDurationMs)) },
        exitTransition = { fadeOut(tween(animationDurationMs)) },
        popEnterTransition = { fadeIn(tween(animationDurationMs)) },
        popExitTransition = { fadeOut(tween(animationDurationMs)) }
    ) {
        val navigator = NavigatorImpl(navController)
        composable("${SearchDestination.route}/${SearchDestination.destination}") {
            SearchRoute(navigator = navigator)
        }
    }
}
