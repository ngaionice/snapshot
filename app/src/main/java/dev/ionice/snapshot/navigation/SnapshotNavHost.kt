package dev.ionice.snapshot.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.ionice.snapshot.core.navigation.EntriesListDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.feature.entries.entriesGraph
import dev.ionice.snapshot.feature.favorites.favoritesGraph
import dev.ionice.snapshot.feature.library.libraryGraph
import dev.ionice.snapshot.feature.search.searchGraph
import dev.ionice.snapshot.feature.settings.settingsGraph
import dev.ionice.snapshot.feature.tags.tagsGraph

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
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
        entriesGraph(navController = navController)

        libraryGraph(navController = navController)

        settingsGraph(navController = navController)

        favoritesGraph(navController = navController)

        searchGraph(navController = navController)

        tagsGraph(navController = navController)
    }
}