package me.ionice.snapshot.ui.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.utils.FakeData
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@Composable
fun MetricsList(keys: List<MetricKey>, onItemClick: (MetricKey) -> Unit) {
    LazyColumn {
        items(items = keys, key = { metricKey -> metricKey.id }) {
            MetricsListItem(key = it) { onItemClick(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsListItem(key: MetricKey, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp), onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = key.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AddKeyDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {

    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add metric type") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Metric name") })
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty()) {
                        onConfirm(name)
                    }
                }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MetricEntriesList(entries: List<MetricEntry>) {
    LazyColumn {
        items(items = entries) { MetricEntriesListItem(entry = it) }
    }
}

@Composable
fun MetricEntriesListItem(entry: MetricEntry) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = Utils.dateFormatter.format(LocalDate.ofEpochDay(entry.dayId)),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
            Text(
                text = entry.value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun MetricEntriesListPreview() {
    MetricEntriesList(entries = FakeData.metricEntries)
}