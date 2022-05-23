package me.ionice.snapshot.ui.metrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@Composable
fun MetricsScreen(viewModel: MetricsViewModel) {

    val uiState by viewModel.uiState.collectAsState()



    if (uiState.loading) {
        BaseScreen(headerText = "Metrics") {
            LoadingScreen()
        }
    } else {
        when (uiState) {
            is MetricsScreenState.MetricList -> {
                MetricListScreen(
                    uiState = uiState as MetricsScreenState.MetricList,
                    onListItemClick = { viewModel.selectMetric(it) },
                    onAddKey = { viewModel.addKey(it) })
            }
            is MetricsScreenState.MetricDetails -> {
                MetricDetailsScreen(uiState as MetricsScreenState.MetricDetails)
            }
        }
    }
}

@Composable
private fun MetricListScreen(
    uiState: MetricsScreenState.MetricList,
    onListItemClick: (MetricKey) -> Unit,
    onAddKey: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    BaseScreen(headerText = "Metrics") {
        Scaffold(backgroundColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                AddKeyFAB {
                    showAddDialog = true
                }
            }) {
            Box(modifier = Modifier.padding(it)) {
                MetricsList(keys = uiState.keys, onItemClick = onListItemClick)
                if (showAddDialog) {
                    AddKeyDialog(onConfirm = { name ->
                        onAddKey(name)
                        showAddDialog = false
                    }, onDismiss = { showAddDialog = false })
                }
            }
        }
    }
}

@Composable
private fun MetricDetailsScreen(uiState: MetricsScreenState.MetricDetails) {

}