package me.ionice.snapshot.ui.days

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.metric.MetricRepository
import me.ionice.snapshot.ui.days.screen.EntryRoute
import me.ionice.snapshot.ui.days.screen.ListRoute
import me.ionice.snapshot.ui.navigation.NavigationDestination

object DayDestination : NavigationDestination {
    override val route = "day_route"
    override val destination = "day_destination"
    const val dayIdArg = "day_id"
    const val listDestination = "list"
    const val viewDestination = "view"
    const val editDestination = "edit"
}

fun NavGraphBuilder.dayGraph(
    navController: NavHostController,
    dayRepository: DayRepository,
    metricRepository: MetricRepository
) {
    navigation(
        route = DayDestination.route,
        startDestination = "${DayDestination.route}/${DayDestination.listDestination}"
    ) {
        composable(route = "${DayDestination.route}/${DayDestination.listDestination}") { _ ->
            ListRoute(
                viewModel = viewModel(
                    factory = DayListViewModel.provideFactory(dayRepository, metricRepository)
                ),
                onSelectItem = { navController.navigate("${DayDestination.route}/$it") })
        }
        composable(
            route = "${DayDestination.route}/{${DayDestination.dayIdArg}}",
            arguments = listOf(navArgument(DayDestination.dayIdArg) { type = NavType.LongType })
        ) {
            EntryRoute(
                viewModel = viewModel(
                    factory = DayEntryViewModel.provideFactory(dayRepository, metricRepository)
                ),
                dayId = it.arguments?.getLong(DayDestination.dayIdArg)!!,
                onBack = { navController.popBackStack() }
            )
        }
    }
}