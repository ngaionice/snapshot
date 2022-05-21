package me.ionice.snapshot.ui.day

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate

@Composable
fun DayScreen(viewModel: DayViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.loading) {
        LoadingScreen()
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
                    onLocationChange = { viewModel.setLocation(it) },
                    onSummaryChange = { viewModel.setSummary(it) },
                    onMetricAdd = { viewModel.addMetric(it) },
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No entry found. Would you like to create one?",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { onDayAdd(uiState.epochDay) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add day entry")
                }
            }
        }
    }
}

@Composable
fun DayAvailableScreen(
    uiState: DayUiState.Available,
    onLocationChange: (String) -> Unit,
    onSummaryChange: (String) -> Unit,
    onMetricAdd: (MetricEntry) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit
) {
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
                onMetricAdd = onMetricAdd,
                onMetricChange = onMetricChange,
                onMetricDelete = onMetricDelete
            )
        }
    }
}

@Composable
fun SectionHeader(imageVector: ImageVector, headerText: String, modifier: Modifier = Modifier) {
    Row(
        modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector, contentDescription = headerText)
        Text(headerText, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun SummaryField(summary: String, setSummary: (String) -> Unit, modifier: Modifier = Modifier) {
    SectionHeader(imageVector = Icons.Filled.EditNote, headerText = "Summary")
    TextField(value = summary, onValueChange = {
        if (it.length <= 140) {
            setSummary(it)
        }
    }, modifier = modifier.fillMaxWidth())
}

@Composable
fun LocationField(location: String?, setLocation: (String) -> Unit, modifier: Modifier = Modifier) {
    SectionHeader(imageVector = Icons.Filled.PinDrop, headerText = "Location")
    TextField(value = location ?: "", onValueChange = {
        if (it.length <= 50) {
            setLocation(it)
        }
    }, modifier = modifier.fillMaxWidth())
}

@Composable
fun MetricList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    modifier: Modifier = Modifier,
    onMetricAdd: (MetricEntry) -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit
) {

    val keyMap by remember(keys) {
        derivedStateOf {
            mutableMapOf<Long, MetricKey>().apply {
                putAll(
                    keys.associateBy(
                        { it.id },
                        { it })
                )
            }
        }
    }

    SectionHeader(imageVector = Icons.Filled.List, headerText = "Metrics")
    entries.mapIndexed { index, entry ->
        val key = keyMap[entry.metricId]
        if (key != null) {
            MetricListItem(
                entry = entry,
                key = key,
                onChange = { onMetricChange(index, it) },
                onDelete = { onMetricDelete(it) })
        }
    }
    MetricListAdd(onClick = { /*TODO*/ })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricListItem(
    entry: MetricEntry,
    key: MetricKey,
    onChange: (String) -> Unit,
    onDelete: (MetricEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = entry.value,
                    onValueChange = onChange,
                    label = { Text(key.name, style = MaterialTheme.typography.labelMedium) })
            }
            IconButton(onClick = { onDelete(entry) }) {
                Icon(Icons.Filled.Close, contentDescription = "Delete")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricListAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.AddCircle,
                contentDescription = "Add new item",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun DayScreenPreview() {
    DayScreen(viewModel())
}