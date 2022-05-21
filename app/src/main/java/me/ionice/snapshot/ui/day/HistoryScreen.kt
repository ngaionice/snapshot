package me.ionice.snapshot.ui.day

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.utils.Utils
import java.time.LocalDate


@Composable
fun HistoryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = viewModel()
) {
    BaseScreen(headerText = "History") {
        DayList(days = historyViewModel.days)
    }
}

@Composable
private fun DayList(days: List<DayWithMetrics>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = days, key = { dayWm -> dayWm.day.id }) { day ->
            DayListItem(dayWm = day) {
                // TODO: add onClick function to move to page
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayListItem(dayWm: DayWithMetrics, onClick: () -> Unit) {
    val date = LocalDate.ofEpochDay(dayWm.day.id)
    val location: String = dayWm.day.location
    val metricCount = dayWm.metrics.size

    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(24.dp)) {
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
