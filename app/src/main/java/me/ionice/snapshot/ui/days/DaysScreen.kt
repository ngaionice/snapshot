package me.ionice.snapshot.ui.days

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaysScreen(viewModel: DaysViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf(false) }

    if (uiState.loading) {
        BaseScreen(headerText = "Day") {
            LoadingScreen()
        }
    } else {
        when (uiState) {
            is DayUiState.DayList -> {
                DayListScreen(
                    uiState = uiState as DayUiState.DayList,
                    onDaySelect = { viewModel.selectDay(it) })
            }
            is DayUiState.DayEntryNotFound -> {
                DayEntryNotAvailableScreen(
                    uiState = uiState as DayUiState.DayEntryNotFound,
                    onDayAdd = { viewModel.addDay(it) }, onBack = { viewModel.deselectDay() })
            }
            is DayUiState.DayEntryFound -> {
                if (editing) {
                    DayEntryEditScreen(
                        uiState = uiState as DayUiState.DayEntryFound,
                        scope = scope,
                        onLocationChange = { viewModel.setLocation(it) },
                        onSummaryChange = { viewModel.setSummary(it) },
                        onMetricAdd = { viewModel.addMetric(it) },
                        onMetricDelete = { viewModel.removeMetric(it) },
                        onMetricChange = { index, value -> viewModel.updateMetric(index, value) },
                        onSave = {
                            editing = false
                            viewModel.saveDay()
                        },
                        onBack = {
                            editing = false
                            viewModel.selectDay((uiState as DayUiState.DayEntryFound).date)
                        })
                } else {
                    DayEntryViewScreen(
                        uiState = uiState as DayUiState.DayEntryFound,
                        onEdit = { editing = true },
                        onBack = { viewModel.deselectDay() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayListScreen(uiState: DayUiState.DayList, onDaySelect: (Long) -> Unit) {
    BaseScreen(headerText = uiState.year.toString()) {
        EntryList(days = uiState.entries, onDaySelect = onDaySelect)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayEntryNotAvailableScreen(
    uiState: DayUiState.DayEntryNotFound,
    onDayAdd: (Long) -> Unit,
    onBack: () -> Unit
) {
    BaseScreen(
        headerText = Utils.formatter.format(LocalDate.ofEpochDay(uiState.date)),
        navigationIcon = { BackButton(onBack) }, floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = { onDayAdd(uiState.date) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add day entry")
            }
        },
        floatingActionButtonPosition = FabPosition.End) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "No entry found.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayEntryViewScreen(
    uiState: DayUiState.DayEntryFound,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    BaseScreen(
        headerText = Utils.formatter.format(LocalDate.ofEpochDay(uiState.date)),
        navigationIcon = { BackButton(onBack) }, floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit entry")
            }
        }) {
        Column(modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocationText(location = uiState.location)
            SummaryText(summary = uiState.summary)
            MetricViewList(
                entries = uiState.metrics,
                keys = uiState.metricKeys
            )
        }

    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DayEntryEditScreen(
    uiState: DayUiState.DayEntryFound,
    scope: CoroutineScope,
    onLocationChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onMetricAdd: (MetricEntry) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val existingMetricIds by derivedStateOf {
        uiState.metrics.map { entry -> entry.metricId }.toSet()
    }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    BackHandler {
        // change back button action to hide bottom sheet instead when it is visible
        if (sheetState.isVisible) {
            scope.launch {
                sheetState.hide()
            }
        } else {
            onBack()
        }
    }

    BottomSheetScaffold(sheetState = sheetState, sheetContent = {
        MetricKeySelector(metricKeys = uiState.metricKeys.filter { key ->
            !existingMetricIds.contains(
                key.id
            )
        }, onSelection = {
            onMetricAdd(
                MetricEntry(it, uiState.date, "")
            )
            scope.launch {
                sheetState.hide()
            }
        })
    }) {
        BaseScreen(
            headerText = Utils.formatter.format(LocalDate.ofEpochDay(uiState.date)),
            navigationIcon = { BackButton(onBack) }, floatingActionButton = {
                androidx.compose.material3.FloatingActionButton(onClick = onSave) {
                    Icon(Icons.Filled.Save, contentDescription = "Save")
                }
            },) {
            Column(
                Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LocationField(
                    location = uiState.location,
                    setLocation = onLocationChange
                )
                SummaryField(
                    summary = uiState.summary,
                    setSummary = onSummaryChange
                )
                MetricEditList(
                    entries = uiState.metrics,
                    keys = uiState.metricKeys,
                    showAddButton = existingMetricIds.size < uiState.metricKeys.size,
                    onShowAddMetricSheet = {
                        scope.launch {
                            sheetState.show()
                        }
                    },
                    onMetricChange = onMetricChange,
                    onMetricDelete = onMetricDelete
                )
            }
        }
    }
}
