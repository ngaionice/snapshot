package me.ionice.snapshot.ui.days.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.utils.Utils.locale
import java.time.LocalDate
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallDayCard(day: DayWithMetrics, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val date = LocalDate.ofEpochDay(day.core.id)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                        .uppercase(locale),
                    color = contentColor
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = contentColor,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = day.metrics.size.toString(), color = contentColor)
                    Icon(imageVector = Icons.Filled.BarChart, contentDescription = "Metrics")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallAddDayCard(dayId: Long, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val date = LocalDate.ofEpochDay(dayId)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                        .uppercase(locale),
                    color = contentColor
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = contentColor,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add day",
                    tint = contentColor
                )
            }
        }

    }
}