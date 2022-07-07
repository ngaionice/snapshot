package me.ionice.snapshot.ui.days

import androidx.navigation.*
import androidx.navigation.compose.composable
import me.ionice.snapshot.ui.days.screen.EditRoute
import me.ionice.snapshot.ui.days.screen.ListRoute
import me.ionice.snapshot.ui.days.screen.ViewRoute
import me.ionice.snapshot.ui.navigation.NavigationDestination

object DayDestination : NavigationDestination {
    override val route = "day_route"
    override val destination = "day_destination"
    const val dayIdArg = "day_id"
    const val listDestination = "list"
    const val viewDestination = "view"
    const val editDestination = "edit"
}

fun NavGraphBuilder.dayGraph(navController: NavHostController, dayListViewModel: DayListViewModel, dayEntryViewModel: DayEntryViewModel) {
    navigation(
        route = DayDestination.route,
        startDestination = "${DayDestination.route}/${DayDestination.listDestination}"
    ) {
        composable(route = "${DayDestination.route}/${DayDestination.listDestination}") { _ ->
            ListRoute(
                viewModel = dayListViewModel,
                onSelectItem = { navController.navigate("${DayDestination.route}/${DayDestination.viewDestination}/$it") })
        }
        composable(
            route = "${DayDestination.route}/${DayDestination.viewDestination}/{${DayDestination.dayIdArg}}",
            arguments = listOf(navArgument(DayDestination.dayIdArg) { type = NavType.LongType })
        ) {
            ViewRoute(
                viewModel = dayEntryViewModel,
                onEditItem = {
                    navController.navigate(
                        "${DayDestination.route}/${DayDestination.editDestination}/${
                            it.arguments?.getLong(DayDestination.dayIdArg)
                        }"
                    )
                },
                onBack = { navController.popBackStack() }, day = it.arguments?.getLong(DayDestination.dayIdArg)!!)
        }
        composable(
            route = "${DayDestination.route}/${DayDestination.editDestination}/{${DayDestination.dayIdArg}}",
            arguments = listOf(navArgument(DayDestination.dayIdArg) { type = NavType.LongType })
        ) {
            EditRoute(viewModel = dayEntryViewModel, onBack = { navController.popBackStack() })
        }
    }
}