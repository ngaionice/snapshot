package dev.ionice.snapshot.feature.entries

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import dev.ionice.snapshot.core.navigation.ENTRIES_ROUTE
import dev.ionice.snapshot.core.navigation.EntriesListDestination
import dev.ionice.snapshot.core.navigation.EntriesSingleDestination
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.feature.entries.list.EntriesListRoute
import dev.ionice.snapshot.feature.entries.single.EntriesSingleRoute

fun NavGraphBuilder.entriesGraph(navController: NavHostController) {
    navigation(
        route = ENTRIES_ROUTE,
        startDestination = "${EntriesListDestination.route}/${EntriesListDestination.destination}",
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
        },
    ) {
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${EntriesListDestination.route}/${EntriesListDestination.destination}",
            enterTransition = { fadeIn(tween(animationDurationMs)) }
        ) {
            EntriesListRoute(navigator = navigator)
        }

        composable(
            route = "${EntriesSingleDestination.route}/${EntriesSingleDestination.destination}/{${EntriesSingleDestination.dayIdArg}}",
            arguments = listOf(navArgument(EntriesSingleDestination.dayIdArg) {
                type = NavType.LongType
            })
        ) {
            EntriesSingleRoute(
                dayId = it.arguments?.getLong(EntriesSingleDestination.dayIdArg)!!,
                navigator = navigator
            )
        }
    }
}