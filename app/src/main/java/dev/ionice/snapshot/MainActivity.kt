package dev.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.ionice.snapshot.core.ui.ElevationTokens
import dev.ionice.snapshot.ui.navigation.SnapshotNavHost
import dev.ionice.snapshot.ui.navigation.SnapshotNavigationBar
import dev.ionice.snapshot.ui.navigation.SnapshotTopLevelNavigation
import dev.ionice.snapshot.ui.theme.SnapshotTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapshotApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

        val systemUiController = rememberSystemUiController()
        val isDarkTheme = isSystemInDarkTheme()
        val navigationBarColor =
            if (appState.shouldShowBottomBar) MaterialTheme.colorScheme.surfaceColorAtElevation(
                ElevationTokens.Level2
            ) else MaterialTheme.colorScheme.background
        systemUiController.setStatusBarColor(
            MaterialTheme.colorScheme.background,
            darkIcons = !isDarkTheme
        )
        systemUiController.setNavigationBarColor(
            navigationBarColor,
            darkIcons = !isDarkTheme
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                AnimatedVisibility(
                    visible = appState.shouldShowBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    SnapshotNavigationBar(
                        onNavigateToTopLevelDestination = snapshotTopLevelNavigation::navigateTo,
                        currentDestination = currentDestination
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SnapshotNavHost(navController = navController)
            }
        }


    }
}

