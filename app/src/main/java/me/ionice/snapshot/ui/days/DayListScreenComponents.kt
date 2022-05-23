package me.ionice.snapshot.ui.days

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate

@Composable
fun EntryList(
    days: List<DayWithMetrics>,
    modifier: Modifier = Modifier,
    onDaySelect: (Long) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = days, key = { day -> day.day.id }) { day ->
            EntryListItem(day = day) { onDaySelect(day.day.id) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryListItem(day: DayWithMetrics, onClick: () -> Unit) {
    val date = LocalDate.ofEpochDay(day.day.id)
    val location: String = day.day.location
    val metricCount = day.metrics.size

    Card(modifier = Modifier.padding(vertical = 8.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = Utils.formatter.format(date),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "$metricCount metric${if (metricCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
            ) {
                Text(text = location, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}