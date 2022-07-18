package me.ionice.snapshot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.days.DayListDestination
import me.ionice.snapshot.ui.days.dayGraph
import me.ionice.snapshot.ui.metrics.metricGraph
import me.ionice.snapshot.ui.settings.settingsGraph

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DayListDestination.route,
        modifier = modifier
    ) {
        dayGraph(
            navController = navController,
            dayRepository = appContainer.dayRepository,
            metricRepository = appContainer.metricRepository
        )

        metricGraph(
            navController = navController,
            metricRepository = appContainer.metricRepository
        )

        settingsGraph(
            navController = navController,
            networkRepository = appContainer.networkRepository,
            preferencesRepository = appContainer.preferencesRepository
        )
    }
}