package me.ionice.snapshot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.days.DayListDestination
import me.ionice.snapshot.ui.days.dayGraph
import me.ionice.snapshot.ui.metrics.MetricsScreen
import me.ionice.snapshot.ui.metrics.MetricsViewModel
import me.ionice.snapshot.ui.navigation.graph.METRIC_ROUTE
import me.ionice.snapshot.ui.navigation.graph.SETTINGS_ROUTE
import me.ionice.snapshot.ui.settings.SettingsScreen
import me.ionice.snapshot.ui.settings.SettingsViewModel

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    toggleBottomNav: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = DayListDestination.route, modifier = modifier) {
        dayGraph(
            navController = navController,
            dayRepository = appContainer.dayRepository,
            metricRepository = appContainer.metricRepository
        )

        composable(METRIC_ROUTE) {
            MetricsScreen(
                viewModel = viewModel(
                    factory = MetricsViewModel.provideFactory(
                        appContainer.metricRepository
                    )
                ), toggleBottomNav = toggleBottomNav
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                viewModel =
                viewModel(
                    factory = SettingsViewModel.provideFactory(
                        appContainer.networkRepository,
                        appContainer.preferencesRepository
                    )
                )
            )
        }
    }
}