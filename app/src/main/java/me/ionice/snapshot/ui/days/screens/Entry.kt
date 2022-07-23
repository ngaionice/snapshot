package me.ionice.snapshot.ui.days.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.days.DayEntryUiState
import me.ionice.snapshot.ui.days.DayEntryViewModel

@Composable
fun EntryRoute(viewModel: DayEntryViewModel, dayId: Long, onBack: () -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    var isEditing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDay(dayId)
    }

    when (uiState) {
        is DayEntryUiState.Loading -> {
            LoadingScreen()
        }
        is DayEntryUiState.EntryNotFound -> {
            EntryNotAvailableScreen(
                uiState = uiState as DayEntryUiState.EntryNotFound,
                onDayAdd = viewModel::insertDay,
                onBack = onBack
            )
        }
        is DayEntryUiState.EntryFound -> {
            EntryScreen(
                uiState = uiState as DayEntryUiState.EntryFound,
                isEditing = isEditing,
                setIsEditing = { isEditing = it },
                onBack = onBack,
                onSave = viewModel::saveDay,
                onChangeLocation = viewModel::setLocation,
                onChangeSummary = viewModel::setSummary,
                onAddMetric = viewModel::addMetric,
                onChangeMetric = viewModel::updateMetric,
                onDeleteMetric = viewModel::removeMetric
            )
        }
    }
}

@Composable
fun EntryScreen(
    uiState: DayEntryUiState.EntryFound,
    isEditing: Boolean,
    setIsEditing: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onChangeLocation: (String) -> Unit,
    onChangeSummary: (String) -> Unit,
    onAddMetric: (MetricEntry) -> Unit,
    onChangeMetric: (Int, String) -> Unit,
    onDeleteMetric: (MetricEntry) -> Unit
) {

    if (isEditing) {
        EditScreen(
            uiState = uiState,
            onLocationChange = onChangeLocation,
            onSummaryChange = onChangeSummary,
            onMetricAdd = onAddMetric,
            onMetricDelete = onDeleteMetric,
            onMetricChange = onChangeMetric,
            onSave = {
                onSave()
                setIsEditing(false)
            },
            onBack = { setIsEditing(false) })
    } else {
        ViewScreen(uiState = uiState, onEdit = { setIsEditing(true) }, onBack = onBack)
    }
}