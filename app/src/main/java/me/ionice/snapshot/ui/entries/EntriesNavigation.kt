package me.ionice.snapshot.ui.entries

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
import me.ionice.snapshot.ui.common.animationDurationMs
import me.ionice.snapshot.ui.entries.list.EntriesListRoute
import me.ionice.snapshot.ui.entries.list.EntriesListViewModel
import me.ionice.snapshot.ui.entries.single.EntriesSingleRoute
import me.ionice.snapshot.ui.entries.single.EntriesSingleViewModel
import me.ionice.snapshot.ui.navigation.NavigationDestination
import me.ionice.snapshot.ui.navigation.NavigatorImpl

const val ENTRIES_ROUTE = "entries"

object EntriesListDestination : NavigationDestination {
    override val route = ENTRIES_ROUTE
    override val destination = "list"
}

object EntriesSingleDestination : NavigationDestination {
    override val route = ENTRIES_ROUTE
    override val destination = "single"
    const val dayIdArg = "dayId"
}



@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.entriesGraph(
    navController: NavHostController,
    dayRepository: DayRepository,
    locationRepository: LocationRepository,
    tagRepository: TagRepository
) {
    navigation(
        route = EntriesListDestination.route,
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
            EntriesListRoute(
                viewModel = viewModel(factory = EntriesListViewModel.provideFactory(dayRepository)),
                navigator = navigator
            )
        }

        composable(
            route = "${EntriesSingleDestination.route}/${EntriesSingleDestination.destination}/{${EntriesSingleDestination.dayIdArg}}",
            arguments = listOf(navArgument(EntriesSingleDestination.dayIdArg) {
                type = NavType.LongType
            })
        ) {
            EntriesSingleRoute(
                viewModel = viewModel(
                    factory = EntriesSingleViewModel.provideFactory(
                        dayRepository, locationRepository, tagRepository
                    )
                ),
                dayId = it.arguments?.getLong(EntriesSingleDestination.dayIdArg)!!,
                navigator = navigator
            )
        }
    }
}