package me.ionice.snapshot.ui.metrics.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.metrics.MetricEntryList
import me.ionice.snapshot.ui.metrics.MetricEntryUiState
import me.ionice.snapshot.ui.metrics.MetricEntryViewModel

@Composable
fun EntryRoute(viewModel: MetricEntryViewModel, metricId: Long, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.selectMetric(metricId)
    }

    when (uiState) {
        is MetricEntryUiState.Loading -> LoadingScreen()
        is MetricEntryUiState.Loaded -> EntryScreen(
            uiState = uiState as MetricEntryUiState.Loaded,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryScreen(uiState: MetricEntryUiState.Loaded, onBack: () -> Unit) {
    BaseScreen(
        headerText = uiState.selectedMetric.key.name,
        navigationIcon = { BackButton(onBack) }) {
        Scaffold(backgroundColor = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(it)) {
                MetricEntryList(entries = uiState.selectedMetric.entries)
            }
        }
    }
}