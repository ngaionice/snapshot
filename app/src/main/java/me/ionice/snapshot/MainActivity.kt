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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.day.DayScreen
import me.ionice.snapshot.ui.day.DayViewModel
import me.ionice.snapshot.ui.history.HistoryScreen
import me.ionice.snapshot.ui.history.HistoryViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotApp(appContainer: AppContainer) {
    SnapshotTheme {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
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

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer
) {

    val dayViewModel: DayViewModel = viewModel(
        factory = DayViewModel.provideFactory(
            appContainer.dayRepository,
            appContainer.metricRepository
        )
    )

    val historyViewModel: HistoryViewModel =
        viewModel(factory = HistoryViewModel.provideFactory(appContainer.dayRepository))

    NavHost(navController = navController, startDestination = Screen.Day.name) {
        composable(Screen.Day.name) {
            DayScreen(viewModel = dayViewModel)
        }

        composable(Screen.History.name) {
            HistoryScreen(viewModel = historyViewModel, onDayClick = {
                dayViewModel.switchDay(it)
                navController.navigate(Screen.Day.name) {
                    launchSingleTop = true
                }
            })
        }

        composable(Screen.Metrics.name) {

        }

        composable(Screen.Settings.name) {

        }
    }
}