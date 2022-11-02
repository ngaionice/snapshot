package dev.ionice.snapshot.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.ui.entries.EntriesListDestination
import dev.ionice.snapshot.ui.entries.entriesGraph
import dev.ionice.snapshot.ui.favorites.favoritesGraph
import dev.ionice.snapshot.ui.library.libraryGraph
import dev.ionice.snapshot.ui.settings.settingsGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SnapshotNavHost(
    navController: NavHostController,
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
        entriesGraph(navController = navController)

        libraryGraph(navController = navController)

        settingsGraph(navController = navController)

        favoritesGraph(navController = navController)
    }
}