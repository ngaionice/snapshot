package me.ionice.snapshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import me.ionice.snapshot.ui.entries.EntriesListDestination
import me.ionice.snapshot.ui.library.LibraryHomeDestination

@Composable
fun rememberSnapshotAppState(navController: NavHostController) = remember(navController) {
    SnapshotAppState(navController)
}

class SnapshotAppState(private val navController: NavHostController) {

    private val barRoutes = listOf(
        EntriesListDestination,
        LibraryHomeDestination
    ).map { "${it.route}/${it.destination}" }

    // Reading this attribute will cause recompositions.
    // Not all routes need to show the bottom bar.
    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route?.let { it in barRoutes }
            ?: true
}