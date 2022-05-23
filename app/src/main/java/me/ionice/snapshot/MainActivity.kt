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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.ui.days.DaysScreen
import me.ionice.snapshot.ui.days.DaysViewModel
import me.ionice.snapshot.ui.metrics.MetricsScreen
import me.ionice.snapshot.ui.metrics.MetricsViewModel
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

@Composable
fun SnapshotNavHost(
    navController: NavHostController,
    appContainer: AppContainer
) {

    val daysViewModel: DaysViewModel = viewModel(
        factory = DaysViewModel.provideFactory(
            appContainer.dayRepository,
            appContainer.metricRepository
        )
    )

    val metricsViewModel: MetricsViewModel =
        viewModel(factory = MetricsViewModel.provideFactory(appContainer.metricRepository))

    NavHost(navController = navController, startDestination = Screen.Days.name) {
        composable(Screen.Days.name) {
            DaysScreen(viewModel = daysViewModel)
        }

        composable(Screen.Metrics.name) {
            MetricsScreen(viewModel = metricsViewModel)
        }

        composable(Screen.Settings.name) {

        }
    }
}