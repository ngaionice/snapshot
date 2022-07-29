package me.ionice.snapshot.ui.days

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.metric.MetricRepository
import me.ionice.snapshot.ui.days.screens.EntryRoute
import me.ionice.snapshot.ui.days.screens.ListRoute
import me.ionice.snapshot.ui.navigation.NavigationDestination

const val DAY_ROUTE = "day"

object DayListDestination : NavigationDestination {
    override val route = DAY_ROUTE
    override val destination = "list"
}

object DayEntryDestination : NavigationDestination {
    override val route = DAY_ROUTE
    override val destination = "entry"
    const val dayIdArg = "dayId"
}

fun NavGraphBuilder.dayGraph(
    navController: NavHostController,
    dayRepository: DayRepository,
    metricRepository: MetricRepository
) {
    navigation(
        route = DayListDestination.route,
        startDestination = "${DayListDestination.route}/${DayListDestination.destination}"
    ) {
        composable(route = "${DayListDestination.route}/${DayListDestination.destination}") { _ ->
            ListRoute(
                viewModel = viewModel(
                    factory = DayListViewModel.provideFactory(dayRepository)
                ),
                onSelectDay = { navController.navigate("${DayListDestination.route}/${DayEntryDestination.destination}/$it") })
        }
        composable(
            route = "${DayListDestination.route}/${DayEntryDestination.destination}/{${DayEntryDestination.dayIdArg}}",
            arguments = listOf(navArgument(DayEntryDestination.dayIdArg) {
                type = NavType.LongType
            })
        ) {
            EntryRoute(
                viewModel = viewModel(
                    factory = DayEntryViewModel.provideFactory(dayRepository, metricRepository)
                ),
                dayId = it.arguments?.getLong(DayEntryDestination.dayIdArg)!!,
                onBack = navController::popBackStack
            )
        }
    }
}