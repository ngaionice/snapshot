package me.ionice.snapshot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.entries.EntriesListDestination
import me.ionice.snapshot.ui.entries.entriesGraph
import me.ionice.snapshot.ui.settings.settingsGraph

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = EntriesListDestination.route,
        modifier = modifier
    ) {
        entriesGraph(
            navController = navController,
            dayRepository = appContainer.dayRepository,
            locationRepository = appContainer.locationRepository,
            tagRepository = appContainer.tagRepository
        )

        settingsGraph(
            navController = navController,
            networkRepository = appContainer.networkRepository,
            preferencesRepository = appContainer.preferencesRepository
        )
    }
}