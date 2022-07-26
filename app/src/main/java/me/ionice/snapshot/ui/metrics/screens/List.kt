package me.ionice.snapshot.ui.metrics.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.components.AddFAB
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.metrics.MetricListUiState
import me.ionice.snapshot.ui.metrics.MetricListViewModel

@Composable
fun ListRoute(viewModel: MetricListViewModel, onSelectItem: (MetricKey) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is MetricListUiState.Loading -> LoadingScreen()
        is MetricListUiState.Loaded -> {
            ListScreen(
                uiState = uiState as MetricListUiState.Loaded,
                onSelectItem = onSelectItem,
                onAddKey = viewModel::addKey
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreen(
    uiState: MetricListUiState.Loaded,
    onSelectItem: (MetricKey) -> Unit,
    onAddKey: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    BaseScreen(headerText = stringResource(R.string.metrics_screen_header), floatingActionButton = {
        AddFAB(
            onClick = { showAddDialog = true },
            description = stringResource(R.string.metrics_screen_add_key)
        )
    }) {
        MetricsList(keys = uiState.keys, onItemClick = onSelectItem)
        if (showAddDialog) {
            AddKeyDialog(onConfirm = { name ->
                onAddKey(name)
                showAddDialog = false
            }, onDismiss = { showAddDialog = false })
        }
    }
}

@Composable
fun MetricsList(keys: List<MetricKey>, onItemClick: (MetricKey) -> Unit) {
    LazyColumn {
        items(items = keys, key = { metricKey -> metricKey.id }) {
            MetricsListItem(key = it, onClick = { onItemClick(it) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsListItem(key: MetricKey, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp), onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = key.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AddKeyDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {

    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.metrics_screen_add_key)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.metrics_screen_add_name_helper)) })
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty()) {
                        onConfirm(name)
                    }
                }) {
                Text(stringResource(R.string.metrics_screen_add_name_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.metrics_screen_add_name_cancel))
            }
        }
    )
}