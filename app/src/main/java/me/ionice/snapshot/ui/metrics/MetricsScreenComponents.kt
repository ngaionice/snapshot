package me.ionice.snapshot.ui.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.utils.FakeData
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate



@Composable
fun MetricEntryList(entries: List<MetricEntry>) {
    if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.common_no_results),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn {
            items(items = entries) { MetricEntryListItem(entry = it) }
        }
    }

}

@Composable
fun MetricEntryListItem(entry: MetricEntry) {
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
    MetricEntryList(entries = FakeData.metricEntries)
}