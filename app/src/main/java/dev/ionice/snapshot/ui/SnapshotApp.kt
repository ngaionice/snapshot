package dev.ionice.snapshot.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.ionice.snapshot.core.ui.theme.SnapshotTheme
import dev.ionice.snapshot.navigation.SnapshotNavHost
import dev.ionice.snapshot.navigation.SnapshotNavigationBar
import dev.ionice.snapshot.navigation.SnapshotTopLevelNavigation

@Composable
fun SnapshotApp() {
  SnapshotTheme {
    val snackbarHostState = remember { SnackbarHostState() }
    val appState = rememberSnapshotAppState(snackbarHostState)
    val navController = appState.navController

    val snapshotTopLevelNavigation =
        remember(navController) { SnapshotTopLevelNavigation(navController) }
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backstackEntry?.destination

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
          AnimatedVisibility(
              visible = appState.shouldShowBottomBar,
              enter = slideInVertically(initialOffsetY = { it }),
              exit = slideOutVertically(targetOffsetY = { it })) {
                SnapshotNavigationBar(
                    onNavigateToTopLevelDestination = snapshotTopLevelNavigation::navigateTo,
                    currentDestination = currentDestination)
              }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
          Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            SnapshotNavHost(navController = navController)
          }
        }


  }
}



