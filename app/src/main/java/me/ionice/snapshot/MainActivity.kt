package me.ionice.snapshot

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.ui.navigation.SnapshotNavHost
import me.ionice.snapshot.ui.navigation.SnapshotNavigationBar
import me.ionice.snapshot.ui.navigation.SnapshotTopLevelNavigation
import me.ionice.snapshot.ui.theme.SnapshotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapshotApp(application)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SnapshotApp(application: Application) {

    val appContainer = (application as SnapshotApplication).container

    SnapshotTheme {
        val navController = rememberNavController()
        val snapshotTopLevelNavigation =
            remember(navController) { SnapshotTopLevelNavigation(navController) }

        val bottomNavBarState = rememberSaveable { mutableStateOf(true) }
        val backstackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backstackEntry?.destination

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                AnimatedVisibility(
                    visible = bottomNavBarState.value,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    SnapshotNavigationBar(
                        onNavigateToTopLevelDestination = snapshotTopLevelNavigation::navigateTo,
                        currentDestination = currentDestination
                    )
                }
            }) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            ) {
                SnapshotNavHost(
                    navController = navController,
                    appContainer = appContainer,
                    toggleBottomNav = { bottomNavBarState.value = it },
                    modifier = Modifier
                        .padding(padding)
                        .consumedWindowInsets(padding)
                )
            }
        }
    }
}

