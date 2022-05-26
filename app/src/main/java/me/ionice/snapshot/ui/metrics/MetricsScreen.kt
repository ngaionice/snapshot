package me.ionice.snapshot.ui.metrics

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.AddFAB
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(viewModel: MetricsViewModel, toggleBottomNav: (Boolean) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.loading) {
        BaseScreen(headerText = "Metrics") {
            LoadingScreen()
        }
    } else {
        when (uiState) {
            is MetricsUiState.MetricList -> {
                MetricListScreen(
                    uiState = uiState as MetricsUiState.MetricList,
                    onListItemClick = { viewModel.selectMetric(it) },
                    onAddKey = { viewModel.addKey(it) })
            }
            is MetricsUiState.MetricDetails -> {
                toggleBottomNav(false)
                MetricDetailsScreen(
                    uiState = uiState as MetricsUiState.MetricDetails,
                    onBack = {
                        viewModel.deselectMetric()
                        toggleBottomNav(true)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricListScreen(
    uiState: MetricsUiState.MetricList,
    onListItemClick: (MetricKey) -> Unit,
    onAddKey: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    BaseScreen(headerText = "Metrics", floatingActionButton = {
        AddFAB(onClick = { showAddDialog = true }, description = "Add metric type")
    }) {
        MetricsList(keys = uiState.keys, onItemClick = onListItemClick)
        if (showAddDialog) {
            AddKeyDialog(onConfirm = { name ->
                onAddKey(name)
                showAddDialog = false
            }, onDismiss = { showAddDialog = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricDetailsScreen(uiState: MetricsUiState.MetricDetails, onBack: () -> Unit) {

    BackHandler {
        onBack()
    }

    BaseScreen(
        headerText = uiState.selectedMetric.key.name,
        navigationIcon = { BackButton(onBack) }) {
        Scaffold(backgroundColor = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(it)) {
                MetricEntriesList(entries = uiState.selectedMetric.entries)
            }
        }
    }

    DisposableEffect(true) {
        onDispose {
            onBack()
        }
    }
}