//package me.ionice.snapshot.ui.days.screens
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.ModalBottomSheetState
//import androidx.compose.material.ModalBottomSheetValue
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.rememberModalBottomSheetState
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import me.ionice.snapshot.R
//import me.ionice.snapshot.data.database.v1.metric.MetricEntry
//import me.ionice.snapshot.data.database.v1.metric.MetricKey
//import me.ionice.snapshot.data.database.model.LocationEntry
//import me.ionice.snapshot.data.database.model.TagEntry
//import me.ionice.snapshot.ui.common.components.BackButton
//import me.ionice.snapshot.ui.common.components.SectionHeader
//import me.ionice.snapshot.ui.common.screens.BaseScreen
//import me.ionice.snapshot.ui.days.DayEntryUiState
//import me.ionice.snapshot.utils.Utils
//import java.time.LocalDate
//
//@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
//@Composable
//fun EditScreen(
//    uiState: DayEntryUiState.EntryFound,
//    onLocationChange: (LocationEntry?) -> Unit,
//    onSummaryChange: (String) -> Unit,
//    onTagAdd: (TagEntry) -> Unit,
//    onTagDelete: (TagEntry) -> Unit,
//    onTagChange: (Int, String) -> Unit,
//    onSave: () -> Unit,
//    onBack: () -> Unit
//) {
//    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
//    val scope = rememberCoroutineScope()
//
//    BackHandler {
//        // change back button action to hide bottom sheet instead when it is visible
//        if (sheetState.isVisible) {
//            scope.launch {
//                sheetState.hide()
//            }
//        } else {
//            onBack()
//        }
//    }
//
//    BaseScreen(
//        headerText = Utils.dateFormatter.format(LocalDate.ofEpochDay(uiState.dayId)),
//        navigationIcon = { BackButton(onBack) },
//        floatingActionButton = {
//            FloatingActionButton(onClick = onSave) {
//                Icon(
//                    Icons.Filled.Save,
//                    contentDescription = stringResource(R.string.day_screen_save_day)
//                )
//            }
//        },
//    ) {
//        EditScreenContent(
//            uiState = uiState,
//            scope = scope,
//            sheetState = sheetState,
//            onLocationChange = onLocationChange,
//            onSummaryChange = onSummaryChange,
//            onTagAdd = onTagAdd,
//            onTagChange = onTagChange,
//            onTagDelete = onTagDelete
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//private fun EditScreenContent(
//    uiState: DayEntryUiState.EntryFound,
//    scope: CoroutineScope,
//    sheetState: ModalBottomSheetState,
//    onLocationChange: (LocationEntry) -> Unit,
//    onSummaryChange: (String) -> Unit,
//    onTagAdd: (TagEntry) -> Unit,
//    onTagChange: (Int, String) -> Unit,
//    onTagDelete: (TagEntry) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .padding(vertical = 16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
////        LocationField(
////            location = uiState.selectedLocation,
////            setLocation = onLocationChange
////        )
//        SummaryField(
//            summary = uiState.day.properties.summary,
//            setSummary = onSummaryChange
//        )
////        MetricEditList(
////            entries = uiState.selectedTags,
////            keys = uiState.availableTags,
////            showAddButton = existingMetricIds.size < uiState.availableTags.size,
////            onShowAddMetricSheet = {
////                scope.launch {
////                    sheetState.show()
////                }
////            },
////            onMetricChange = onTagChange,
////            onMetricDelete = onTagDelete
////        )
//    }
//}
//
//@Composable
//private fun SummaryField(
//    summary: String,
//    setSummary: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    SectionHeader(
//        icon = Icons.Filled.EditNote,
//        displayText = stringResource(R.string.day_screen_summary_header)
//    )
//    TextField(
//        value = summary, onValueChange = {
//            if (it.length <= 140) {
//                setSummary(it)
//            }
//        }, modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 24.dp)
//    )
//}
//
//@Composable
//private fun LocationField(
//    location: String?,
//    setLocation: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    SectionHeader(
//        icon = Icons.Filled.PinDrop,
//        displayText = stringResource(R.string.day_screen_location_header)
//    )
//    TextField(
//        value = location ?: "", onValueChange = {
//            if (it.length <= 50) {
//                setLocation(it)
//            }
//        }, modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 24.dp)
//    )
//}
//
//@Composable
//private fun MetricEditList(
//    entries: List<MetricEntry>,
//    keys: List<MetricKey>,
//    showAddButton: Boolean,
//    onShowAddMetricSheet: () -> Unit,
//    onMetricDelete: (MetricEntry) -> Unit,
//    onMetricChange: (Int, String) -> Unit
//) {
//
//    val keyMap by remember(keys) {
//        derivedStateOf {
//            keys.associateBy({ it.id }, { it })
//        }
//    }
//
//    SectionHeader(
//        icon = Icons.Filled.List,
//        displayText = stringResource(R.string.day_screen_metrics_header)
//    )
//    entries.mapIndexed { index, entry ->
//        val key = keyMap[entry.metricId]
//        if (key != null) {
//            MetricEditListItem(
//                entry = entry,
//                key = key,
//                onChange = { onMetricChange(index, it) },
//                onDelete = { onMetricDelete(it) })
//        }
//    }
//    if (showAddButton) {
//        MetricEditListAdd(onClick = { onShowAddMetricSheet() })
//    }
//}
//
//@Composable
//private fun MetricEditListItem(
//    entry: MetricEntry,
//    key: MetricKey,
//    onChange: (String) -> Unit,
//    onDelete: (MetricEntry) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier.padding(vertical = 4.dp, horizontal = 24.dp)
//    ) {
//        Column(
//            verticalArrangement = Arrangement.spacedBy(4.dp),
//            modifier = Modifier.weight(1f)
//        ) {
//            TextField(
//                value = entry.value,
//                onValueChange = onChange,
//                label = { Text(key.name, style = MaterialTheme.typography.labelMedium) })
//        }
//        IconButton(onClick = { onDelete(entry) }) {
//            Icon(
//                Icons.Filled.Close,
//                contentDescription = stringResource(R.string.day_screen_delete_metric)
//            )
//        }
//    }
//}
//
//@Composable
//private fun MetricEditListAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
//    Row(
//        modifier = modifier
//            .clickable(onClick = onClick)
//            .fillMaxWidth()
//            .padding(horizontal = 24.dp, vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            Icons.Filled.AddCircle,
//            contentDescription = stringResource(R.string.day_screen_add_metric),
//            modifier = Modifier.padding(8.dp)
//        )
//    }
//}
//
//@Composable
//private fun MetricKeySelector(metricKeys: List<MetricKey>, onSelection: (Long) -> Unit) {
//    LazyColumn {
//        items(items = metricKeys) { key ->
//            MetricKeySelectionListItem(metricKey = key, onSelection = { onSelection(key.id) })
//        }
//    }
//}
//
//@Composable
//private fun MetricKeySelectionListItem(metricKey: MetricKey, onSelection: () -> Unit) {
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .clickable { onSelection() }
//        .padding(horizontal = 24.dp, vertical = 16.dp)
//    ) {
//        Text(text = metricKey.name)
//    }
//}