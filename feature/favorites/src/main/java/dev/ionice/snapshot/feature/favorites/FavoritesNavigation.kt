package dev.ionice.snapshot.feature.favorites

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.ionice.snapshot.core.navigation.NavigationDestination
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.ui.animationDurationMs

object FavoritesDestination : NavigationDestination {
    override val route = "favorites"
    override val destination = "home"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.favoritesGraph(navController: NavHostController) {
    navigation(
        route = FavoritesDestination.route,
        startDestination = "${FavoritesDestination.route}/${FavoritesDestination.destination}",
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
        composable("${FavoritesDestination.route}/${FavoritesDestination.destination}") {
            FavoritesRoute(navigator = navigator)
        }
    }
}