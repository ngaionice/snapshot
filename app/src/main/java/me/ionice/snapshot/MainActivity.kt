package me.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.data.backup.BackupUtil
import me.ionice.snapshot.ui.navigation.BottomNavigation
import me.ionice.snapshot.ui.navigation.SnapshotNavHost
import me.ionice.snapshot.ui.theme.SnapshotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as SnapshotApplication).container
        val backupUtil = BackupUtil(application.applicationContext)
        setContent {
            SnapshotApp(appContainer, backupUtil)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotApp(appContainer: AppContainer, backupUtil: BackupUtil) {
    SnapshotTheme {
        val navController = rememberNavController()

        val bottomNavBarState = rememberSaveable { mutableStateOf(true) }
//        val backstackEntry = navController.currentBackStackEntryAsState()
        Scaffold(bottomBar = {
            AnimatedVisibility(
                visible = bottomNavBarState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavigation(
                    navController = navController
                )
            }
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SnapshotNavHost(
                    navController = navController,
                    appContainer = appContainer,
                    backupUtil = backupUtil,
                    toggleBottomNav = { bottomNavBarState.value = it }
                )
            }
        }
    }
}

