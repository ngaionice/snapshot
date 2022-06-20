package me.ionice.snapshot.ui.days.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.R
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.SectionHeader
import me.ionice.snapshot.ui.days.*
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@Composable
fun EntryScreen(
    uiState: DayUiState.DayEntryFound,
    onLocationChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onMetricAdd: (MetricEntry) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit,
    onEntrySave: () -> Unit,
    onEntryExit: () -> Unit
) {

    var isEditing by rememberSaveable { mutableStateOf(false) }

    if (isEditing) {
        EditScreen(
            uiState = uiState,
            onLocationChange = onLocationChange,
            onSummaryChange = onSummaryChange,
            onMetricAdd = onMetricAdd,
            onMetricDelete = onMetricDelete,
            onMetricChange = onMetricChange,
            onSave = {
                isEditing = false
                onEntrySave()
            },
            onBack = { isEditing = false })
    } else {
        ViewScreen(uiState = uiState, onEdit = { isEditing = true }, onBack = onEntryExit)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewScreen(
    uiState: DayUiState.DayEntryFound,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    BaseScreen(
        headerText = LocalDate.ofEpochDay(uiState.date).format(Utils.dateFormatter),
        navigationIcon = { BackButton(onBack) },
        floatingActionButton = { EditFAB(onClick = onEdit) })
    {
        ViewScreenContent(
            location = uiState.location,
            summary = uiState.summary,
            metrics = uiState.metrics,
            metricKeys = uiState.metricKeys
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun EditScreen(
    uiState: DayUiState.DayEntryFound,
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
    val scope = rememberCoroutineScope()

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
        MetricKeySelector(
            metricKeys = uiState.metricKeys.filter { key ->
                !existingMetricIds.contains(key.id)
            },
            onSelection = {
                onMetricAdd(MetricEntry(it, uiState.date, ""))
                scope.launch {
                    sheetState.hide()
                }
            })
    }) {
        BaseScreen(
            headerText = Utils.dateFormatter.format(LocalDate.ofEpochDay(uiState.date)),
            navigationIcon = { BackButton(onBack) },
            floatingActionButton = {
                FloatingActionButton(onClick = onSave) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = stringResource(R.string.day_screen_save_day)
                    )
                }
            },
        ) {
            EditScreenContent(
                uiState = uiState,
                scope = scope,
                existingMetricIds = existingMetricIds,
                sheetState = sheetState,
                onLocationChange = onLocationChange,
                onSummaryChange = onSummaryChange,
                onMetricChange = onMetricChange,
                onMetricDelete = onMetricDelete
            )
        }
    }
}

@Composable
private fun ViewScreenContent(
    location: String,
    summary: String,
    metrics: List<MetricEntry>,
    metricKeys: List<MetricKey>
) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LocationText(location = location)
        SummaryText(summary = summary)
        MetricViewList(
            entries = metrics,
            keys = metricKeys
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EditScreenContent(
    uiState: DayUiState.DayEntryFound,
    scope: CoroutineScope,
    existingMetricIds: Set<Long>,
    sheetState: ModalBottomSheetState,
    onLocationChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onMetricChange: (Int, String) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
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

@Composable
private fun EditFAB(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.day_screen_edit_day))
    }
}

@Composable
private fun SummaryText(summary: String, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.EditNote,
        displayText = stringResource(R.string.day_screen_summary_header)
    )
    Text(
        text = summary, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun LocationText(location: String, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.PinDrop,
        displayText = stringResource(R.string.day_screen_location_header)
    )
    Text(
        text = location, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun SummaryField(summary: String, setSummary: (String) -> Unit, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.EditNote,
        displayText = stringResource(R.string.day_screen_summary_header)
    )
    TextField(
        value = summary, onValueChange = {
            if (it.length <= 140) {
                setSummary(it)
            }
        }, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun LocationField(location: String?, setLocation: (String) -> Unit, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.PinDrop,
        displayText = stringResource(R.string.day_screen_location_header)
    )
    TextField(
        value = location ?: "", onValueChange = {
            if (it.length <= 50) {
                setLocation(it)
            }
        }, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun MetricViewList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    modifier: Modifier = Modifier
) {
    val keyMap by remember(keys) {
        derivedStateOf {
            keys.associateBy({ it.id }, { it })
        }
    }

    Column(modifier = modifier) {
        SectionHeader(
            icon = Icons.Filled.List,
            displayText = stringResource(R.string.day_screen_metrics_header)
        )
        entries.map { entry ->
            val key = keyMap[entry.metricId]
            if (key != null) {
                MetricViewListItem(
                    entry = entry,
                    key = key
                )
            }
        }
    }
}

@Composable
private fun MetricViewListItem(entry: MetricEntry, key: MetricKey) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = key.name,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
            Text(
                text = entry.value,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun MetricEditList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    showAddButton: Boolean,
    onShowAddMetricSheet: () -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit
) {

    val keyMap by remember(keys) {
        derivedStateOf {
            keys.associateBy({ it.id }, { it })
        }
    }

    SectionHeader(
        icon = Icons.Filled.List,
        displayText = stringResource(R.string.day_screen_metrics_header)
    )
    entries.mapIndexed { index, entry ->
        val key = keyMap[entry.metricId]
        if (key != null) {
            MetricEditListItem(
                entry = entry,
                key = key,
                onChange = { onMetricChange(index, it) },
                onDelete = { onMetricDelete(it) })
        }
    }
    if (showAddButton) {
        MetricEditListAdd(onClick = { onShowAddMetricSheet() })
    }
}

@Composable
private fun MetricEditListItem(
    entry: MetricEntry,
    key: MetricKey,
    onChange: (String) -> Unit,
    onDelete: (MetricEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = entry.value,
                onValueChange = onChange,
                label = { Text(key.name, style = androidx.compose.material3.MaterialTheme.typography.labelMedium) })
        }
        androidx.compose.material3.IconButton(onClick = { onDelete(entry) }) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.day_screen_delete_metric)
            )
        }
    }
}

@Composable
private fun MetricEditListAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.AddCircle,
            contentDescription = stringResource(R.string.day_screen_add_metric),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun MetricKeySelectionListItem(metricKey: MetricKey, onSelection: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelection() }
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(text = metricKey.name)
    }
}

@Composable
private fun MetricKeySelector(metricKeys: List<MetricKey>, onSelection: (Long) -> Unit) {
    LazyColumn {
        items(items = metricKeys) { key ->
            MetricKeySelectionListItem(metricKey = key, onSelection = { onSelection(key.id) })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetScaffold(
    sheetState: ModalBottomSheetState,
    sheetContent: @Composable () -> Unit,
    screenContent: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
        sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) {
        screenContent()
    }
}