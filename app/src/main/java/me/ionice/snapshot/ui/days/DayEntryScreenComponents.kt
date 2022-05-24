package me.ionice.snapshot.ui.days

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey

@Composable
private fun SectionHeader(
    imageVector: ImageVector,
    headerText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector, contentDescription = headerText)
        Text(headerText, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun SummaryText(summary: String, modifier: Modifier = Modifier) {
    SectionHeader(imageVector = Icons.Filled.EditNote, headerText = "Summary")
    Text(text = summary, modifier = modifier.fillMaxWidth())
}

@Composable
fun LocationText(location: String, modifier: Modifier = Modifier) {
    SectionHeader(imageVector = Icons.Filled.PinDrop, headerText = "Location")
    Text(text = location, modifier = modifier.fillMaxWidth())
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
fun MetricViewList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    modifier: Modifier = Modifier
) {
    val keyMap by remember(keys) {
        derivedStateOf {
            keys.associateBy({ it.id }, { it })
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(imageVector = Icons.Filled.List, headerText = "Metrics")
        entries.mapIndexed { index, entry ->
            val key = keyMap[entry.metricId]
            if (key != null) {
                MetricViewListItem(
                    entry = entry,
                    key = key
                )
                if (index < entries.lastIndex) {
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun MetricViewListItem(entry: MetricEntry, key: MetricKey) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = key.name,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
            Text(
                text = entry.value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MetricEditList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    showAddButton: Boolean,
    onShowAddMetricSheet: () -> Unit,
    onMetricDelete: (MetricEntry) -> Unit,
    onMetricChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyMap by remember(keys) {
        derivedStateOf {
            keys.associateBy({ it.id }, { it })
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(imageVector = Icons.Filled.List, headerText = "Metrics")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricEditListItem(
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
private fun MetricEditListAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
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

@Composable
private fun MetricKeySelectionListItem(metricKey: MetricKey, onSelection: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelection() }
        .padding(16.dp)
    ) {
        Text(text = metricKey.name)
    }
}

@Composable
fun MetricKeySelector(metricKeys: List<MetricKey>, onSelection: (Long) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = metricKeys) { key ->
            MetricKeySelectionListItem(metricKey = key, onSelection = { onSelection(key.id) })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetScaffold(
    sheetState: ModalBottomSheetState,
    sheetContent: @Composable () -> Unit,
    screenContent: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) {
        screenContent()
    }
}