package me.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import me.ionice.snapshot.ui.navigation.SnapshotNavHost
import me.ionice.snapshot.ui.navigation.SnapshotNavigationBar
import me.ionice.snapshot.ui.navigation.SnapshotTopLevelNavigation
import me.ionice.snapshot.ui.theme.SnapshotTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapshotApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SnapshotApp() {

    SnapshotTheme {
        val systemUiController = rememberSystemUiController()
        val isDarkTheme = isSystemInDarkTheme()
        systemUiController.setSystemBarsColor(
            MaterialTheme.colorScheme.background,
            darkIcons = !isDarkTheme
        )

        val navController = rememberAnimatedNavController()
        val appState = rememberSnapshotAppState(navController = navController)
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
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    SnapshotNavigationBar(
                        onNavigateToTopLevelDestination = snapshotTopLevelNavigation::navigateTo,
                        currentDestination = currentDestination
                    )
                }
            }) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ) {
                SnapshotNavHost(
                    navController = navController,
                    modifier = Modifier
                )
            }
        }
    }
}

