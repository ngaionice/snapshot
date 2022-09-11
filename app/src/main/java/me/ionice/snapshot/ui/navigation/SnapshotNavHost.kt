package me.ionice.snapshot.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.common.animationDurationMs
import me.ionice.snapshot.ui.entries.EntriesListDestination
import me.ionice.snapshot.ui.entries.entriesGraph
import me.ionice.snapshot.ui.settings.settingsGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = EntriesListDestination.route,
        enterTransition = { fadeIn(tween(animationDurationMs)) },
        exitTransition = { fadeOut(tween(animationDurationMs)) },
        popEnterTransition = { fadeIn(tween(animationDurationMs)) },
        popExitTransition = { fadeOut(tween(animationDurationMs)) },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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