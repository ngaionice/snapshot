package me.ionice.snapshot.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate


@Composable
fun HistoryScreen(viewModel: HistoryViewModel, onDayClick: (Long) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(headerText = "History") {
        DayList(days = uiState.days, onDayClick = { onDayClick(it) })
    }
}

@Composable
private fun DayList(days: List<DayWithMetrics>, modifier: Modifier = Modifier, onDayClick: (Long) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(items = days, key = { day -> day.day.id }) { day ->
            DayListItem(day = day) { onDayClick(day.day.id) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayListItem(day: DayWithMetrics, onClick: () -> Unit) {
    val date = LocalDate.ofEpochDay(day.day.id)
    val location: String = day.day.location
    val metricCount = day.metrics.size

    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp), onClick = onClick) {
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
