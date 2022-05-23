package me.ionice.snapshot.ui.day

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayScreen(viewModel: DayViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // change back button action to hide bottom sheet instead when it is visible
    BackHandler(enabled = sheetState.isVisible) {
        scope.launch {
            sheetState.hide()
        }
    }

    if (uiState.loading) {
        BaseScreen(headerText = "Day") {
            LoadingScreen()
        }
    } else {
        when (uiState) {
            is DayUiState.NotAvailable -> {
                DayNotAvailableScreen(
                    uiState = uiState as DayUiState.NotAvailable,
                    onDayAdd = { viewModel.addDay(it) })
            }
            is DayUiState.Available -> {
                DayAvailableScreen(
                    uiState = uiState as DayUiState.Available,
                    sheetState = sheetState,
                    onLocationChange = { viewModel.setLocation(it) },
                    onSummaryChange = { viewModel.setSummary(it) },
                    onShowAddMetricSheet = {
                        scope.launch {
                            sheetState.show()
                        }
                    },
                    onMetricAdd = {
                        viewModel.addMetric(it)
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                    onMetricDelete = { viewModel.removeMetric(it) },
                    onMetricChange = { index, value -> viewModel.updateMetric(index, value) })
            }
        }
    }
}

@Composable
fun DayNotAvailableScreen(uiState: DayUiState.NotAvailable, onDayAdd: (Long) -> Unit) {
    if (uiState.epochDay == null) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "You're not supposed to see this screen. How did you get here?")
        }
    } else {
        BaseScreen(headerText = Utils.formatter.format(LocalDate.ofEpochDay(uiState.epochDay))) {
            Scaffold(
                floatingActionButton = {
                    androidx.compose.material3.FloatingActionButton(onClick = { onDayAdd(uiState.epochDay) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add day entry")
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
                backgroundColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "No entry found.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayAvailableScreen(
    uiState: DayUiState.Available,
    sheetState: ModalBottomSheetState,
    onLocationChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onShowAddMetricSheet: () -> Unit,
    onMetricAdd: (MetricEntry) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit
) {
    BottomSheetScaffold(sheetState = sheetState, sheetContent = {
        MetricKeySelector(metricKeys = uiState.metricKeys, onSelection = {
            onMetricAdd(
                MetricEntry(it, uiState.epochDay, "")
            )
        })
    }) {
        BaseScreen(headerText = Utils.formatter.format(LocalDate.ofEpochDay(uiState.epochDay))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LocationField(
                    location = uiState.location,
                    setLocation = onLocationChange
                )
                SummaryField(
                    summary = uiState.summary,
                    setSummary = onSummaryChange
                )
                MetricList(
                    entries = uiState.metrics,
                    keys = uiState.metricKeys,
                    onShowAddMetricSheet = onShowAddMetricSheet,
                    onMetricChange = onMetricChange,
                    onMetricDelete = onMetricDelete
                )
            }
        }
    }
}

@Preview
@Composable
fun DayScreenPreview() {
    DayScreen(viewModel())
}