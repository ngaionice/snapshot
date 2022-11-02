package dev.ionice.snapshot.ui.entries

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.ionice.snapshot.core.navigation.ENTRIES_ROUTE
import dev.ionice.snapshot.core.navigation.EntriesListDestination
import dev.ionice.snapshot.core.navigation.EntriesSingleDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.ui.entries.list.EntriesListRoute
import dev.ionice.snapshot.ui.entries.single.EntriesSingleRoute
import dev.ionice.snapshot.core.navigation.NavigatorImpl

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.entriesGraph(navController: NavHostController) {
    navigation(
        route = ENTRIES_ROUTE,
        startDestination = "${EntriesListDestination.route}/${EntriesListDestination.destination}",
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