//package me.ionice.snapshot.ui.metrics
//
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.*
//import androidx.navigation.compose.composable
//import me.ionice.snapshot.data.database.v1.metric.MetricRepository
//import me.ionice.snapshot.ui.metrics.screens.EntryRoute
//import me.ionice.snapshot.ui.metrics.screens.ListRoute
//import me.ionice.snapshot.ui.navigation.NavigationDestination
//
//const val METRIC_ROUTE = "metric"
//
//object MetricListDestination : NavigationDestination {
//    override val route = METRIC_ROUTE
//    override val destination = "list"
//}
//
//object MetricEntryDestination : NavigationDestination {
//    override val route = METRIC_ROUTE
//    override val destination = "entry"
//    const val metricIdArg = "metricId"
//}
//
//fun NavGraphBuilder.metricGraph(
//    navController: NavHostController,
//    metricRepository: MetricRepository
//) {
//    navigation(
//        route = METRIC_ROUTE,
//        startDestination = "${MetricListDestination.route}/${MetricListDestination.destination}"
//    ) {
//        composable(route = "${MetricListDestination.route}/${MetricListDestination.destination}") { _ ->
//            ListRoute(
//                viewModel = viewModel(
//                    factory = MetricListViewModel.provideFactory(
//                        metricRepository
//                    )
//                ),
//                onSelectItem = { navController.navigate("${MetricEntryDestination.route}/${MetricEntryDestination.destination}/${it.id}") })
//        }
//
//        composable(
//            route = "${MetricEntryDestination.route}/${MetricEntryDestination.destination}/{${MetricEntryDestination.metricIdArg}}",
//            arguments = listOf(
//                navArgument(MetricEntryDestination.metricIdArg) { type = NavType.LongType })
//        ) {
//            EntryRoute(
//                viewModel = viewModel(
//                    factory = MetricEntryViewModel.provideFactory(
//                        metricRepository
//                    )
//                ),
//                metricId = it.arguments?.getLong(MetricEntryDestination.metricIdArg)!!,
//                onBack = navController::popBackStack
//            )
//        }
//    }
//}