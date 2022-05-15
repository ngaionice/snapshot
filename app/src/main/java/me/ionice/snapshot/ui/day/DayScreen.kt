package me.ionice.snapshot.ui.day

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun DayScreen(viewModel: DayViewModel = viewModel()) {
    BaseScreen(headerText = Utils.formatter.format(viewModel.date)) {
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
    SectionHeader(imageVector = Icons.Filled.List, headerText = "Metrics")

}

@Preview
@Composable
fun DayScreenPreview() {
    DayScreen()
}