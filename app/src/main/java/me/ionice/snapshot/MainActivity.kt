package me.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.ui.navigation.Screen
import me.ionice.snapshot.ui.day.DayScreen
import me.ionice.snapshot.ui.day.HistoryScreen
import me.ionice.snapshot.ui.navigation.BottomNavigation
import me.ionice.snapshot.ui.theme.SnapshotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapshotApp()
        }
    }
}

val navOptions = listOf(
    Screen.Today,
    Screen.History,
    Screen.Metrics,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotApp() {
    SnapshotTheme {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        Scaffold(bottomBar = { BottomNavigation(navController = navController, options = navOptions)}) {
            innerPadding -> SnapshotNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun SnapshotNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Today.name) {
        composable(Screen.Today.name) {
            DayScreen()
        }

        composable(Screen.History.name) {
            HistoryScreen(navController)
        }

        composable(Screen.Metrics.name) {

        }

        composable(Screen.Settings.name) {

        }
    }
}