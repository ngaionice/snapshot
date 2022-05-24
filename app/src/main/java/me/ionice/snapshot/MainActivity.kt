package me.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.navigation.BottomNavigation
import me.ionice.snapshot.ui.navigation.SnapshotNavHost
import me.ionice.snapshot.ui.theme.SnapshotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as SnapshotApplication).container
        setContent {
            SnapshotApp(appContainer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotApp(appContainer: AppContainer) {
    SnapshotTheme {
        val navController = rememberNavController()
//        val backstackEntry = navController.currentBackStackEntryAsState()
        Scaffold(bottomBar = {
            BottomNavigation(
                navController = navController
            )
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SnapshotNavHost(
                    navController = navController,
                    appContainer = appContainer
                )
            }
        }
    }
}

