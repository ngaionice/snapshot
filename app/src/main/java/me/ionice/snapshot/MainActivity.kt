package me.ionice.snapshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.day.DayScreen
import me.ionice.snapshot.ui.day.DayViewModel
import me.ionice.snapshot.ui.history.HistoryScreen
import me.ionice.snapshot.ui.navigation.BottomNavigation
import me.ionice.snapshot.ui.navigation.Screen
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

val navOptions = listOf(
    Screen.Today,
    Screen.History,
    Screen.Metrics,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotApp(appContainer: AppContainer) {
    SnapshotTheme {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        Scaffold(bottomBar = {
            BottomNavigation(
                navController = navController,
                options = navOptions
            )
        }) { innerPadding ->
            SnapshotNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                appContainer = appContainer
            )
        }
    }
}

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    appContainer: AppContainer
) {
    NavHost(navController = navController, startDestination = Screen.Today.name) {
        composable(Screen.Today.name) {
            DayScreen(
                viewModel = viewModel(
                    factory = DayViewModel.provideFactory(
                        appContainer.dayRepository,
                        appContainer.metricRepository
                    )
                )
            )
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