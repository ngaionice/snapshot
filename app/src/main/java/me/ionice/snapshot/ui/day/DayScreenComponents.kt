package me.ionice.snapshot.ui.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.metric.Metric
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey

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

    Column(modifier = modifier) {
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
        MetricListAdd(onClick = { onShowAddMetricSheet() })
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricKeySelectionDialog(metricKeys: List<MetricKey>, onSelection: (Long) -> Unit) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedOption by remember { mutableStateOf(MetricKey(id = -1, name = "")) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = selectedOption.name,
                onValueChange = { },
                label = { Text("Label") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                metricKeys.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = selectionOption
                            expanded = false
                        }
                    ) {
                        Text(text = selectionOption.name)
                    }
                }
            }
        }
        Button(onClick = {
            if (selectedOption.id != -1L) {
                onSelection(selectedOption.id)
            }
        }) {
            Text("Add")
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
        sheetContent = { sheetContent() }
    ) {
        screenContent()
    }
}