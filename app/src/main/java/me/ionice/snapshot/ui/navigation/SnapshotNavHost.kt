package me.ionice.snapshot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.days.DayDestination
import me.ionice.snapshot.ui.days.DayEntryViewModel
import me.ionice.snapshot.ui.days.DayListViewModel
import me.ionice.snapshot.ui.days.dayGraph
import me.ionice.snapshot.ui.metrics.MetricsScreen
import me.ionice.snapshot.ui.metrics.MetricsViewModel
import me.ionice.snapshot.ui.settings.SettingsScreen
import me.ionice.snapshot.ui.settings.SettingsViewModel

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    toggleBottomNav: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = DayDestination.route) {
        dayGraph(
            navController = navController,
            dayRepository = appContainer.dayRepository,
            metricRepository = appContainer.metricRepository
        )

        composable(Screen.Metrics.name) {
            MetricsScreen(
                viewModel = viewModel(
                    factory = MetricsViewModel.provideFactory(
                        appContainer.metricRepository
                    )
                ), toggleBottomNav = toggleBottomNav
            )
        }

        composable(Screen.Settings.name) {
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