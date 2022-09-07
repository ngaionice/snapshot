package me.ionice.snapshot.ui.entries

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
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

fun NavGraphBuilder.entriesGraph(
    navController: NavHostController,
    dayRepository: DayRepository,
    locationRepository: LocationRepository,
    tagRepository: TagRepository
) {
    navigation(
        route = EntriesListDestination.route,
        startDestination = "${EntriesListDestination.route}/${EntriesListDestination.destination}"
    ) {
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${EntriesListDestination.route}/${EntriesListDestination.destination}"
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