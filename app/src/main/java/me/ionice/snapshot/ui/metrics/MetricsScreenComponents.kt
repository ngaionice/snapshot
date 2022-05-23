package me.ionice.snapshot.ui.metrics

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.metric.MetricKey

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
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), onClick = onClick) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = key.name, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun AddKeyFAB(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add metric type")
    }
}

@Composable
fun AddKeyDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {

    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add metric type") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = {Text("Metric name")})
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