package me.ionice.snapshot.ui.favorites

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import me.ionice.snapshot.ui.common.animationDurationMs
import me.ionice.snapshot.ui.navigation.NavigationDestination
import me.ionice.snapshot.ui.navigation.NavigatorImpl

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