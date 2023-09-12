package dev.ionice.snapshot.feature.tags

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.navigation.TAGS_ROUTE
import dev.ionice.snapshot.core.navigation.TagsListDestination
import dev.ionice.snapshot.core.navigation.TagsSingleDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.feature.tags.screens.TagsListRoute
import dev.ionice.snapshot.feature.tags.screens.TagsSingleRoute

fun NavGraphBuilder.tagsGraph(navController: NavHostController) {
    navigation(route = TAGS_ROUTE,
        startDestination = "${TagsListDestination.route}/${TagsListDestination.destination}"
    ) {
        val navigator = NavigatorImpl(navController)
        composable(route = "${TagsListDestination.route}/${TagsListDestination.destination}",
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
            TagsListRoute(navigator = navigator)
        }
        composable(route = "${TagsSingleDestination.route}/${TagsSingleDestination.destination}/{${TagsSingleDestination.tagIdArg}}",
            arguments = listOf(
                navArgument(TagsSingleDestination.tagIdArg) { type = NavType.LongType }
            ),
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
            TagsSingleRoute(
                tagId = it.arguments!!.getLong(TagsSingleDestination.tagIdArg),
                navigator = navigator
            )
        }
    }
}