package me.ionice.snapshot.ui.day

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.ionice.snapshot.database.MetricEntry
import me.ionice.snapshot.database.MetricKey
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.utils.Data
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate

@Composable
fun DayScreen(viewModel: DayViewModel = viewModel()) {
    BaseScreen(headerText = Utils.formatter.format(LocalDate.ofEpochDay(viewModel.date))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LocationField(location = viewModel.location, setLocation = { viewModel.location = it })
            SummaryField(summary = viewModel.summary, setSummary = { viewModel.summary = it })
            MetricList(entries = viewModel.metrics, keys = Data.metricKeys)
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
fun MetricList(entries: List<MetricEntry>, keys: List<MetricKey>, modifier: Modifier = Modifier) {

    val keyMap = remember { mutableStateMapOf<Long, MetricKey>().apply { putAll(keys.associateBy({it.id}, {it})) } }

    SectionHeader(imageVector = Icons.Filled.List, headerText = "Metrics")
    // TODO: take in insert/delete/edit functions for the list
    entries.map {
        val key = keyMap[it.metricId]
        if (key != null) {
            MetricListItem(entry = it, key = key, onChange = { /*TODO*/ }, onDelete = { /*TODO*/ })
        }
    }
    MetricListAdd(onClick = { /*TODO*/ })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricListItem(entry: MetricEntry, key: MetricKey, onChange: (String) -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                TextField(value = entry.value, onValueChange = onChange, label = { Text(key.name, style = MaterialTheme.typography.labelMedium) })
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Close, contentDescription = "Delete")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricListAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Filled.AddCircle, contentDescription = "Add new item", modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview
@Composable
fun DayScreenPreview() {
    DayScreen()
}