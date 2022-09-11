package me.ionice.snapshot.ui.entries.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.entries.list.WeekUiState
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate
import java.time.format.TextStyle

private const val CARD_WIDTH = 0.3333f

@Composable
fun ThisWeek(
    uiStateProvider: () -> WeekUiState,
    onAddEntry: (Long) -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    val uiState = uiStateProvider()

    if (uiState is WeekUiState.Loading) {
        LazyRow(
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = 3) {
                PlaceholderThisWeekCard(modifier = Modifier.fillParentMaxWidth(CARD_WIDTH))
            }
        }
        return
    }

    if (uiState is WeekUiState.Error) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load data for the past week.")
        }
        return
    }

    val today = LocalDate.now().toEpochDay()
    val dateRange = (today downTo today - 6).toList()
    val entries = (uiState as WeekUiState.Success).entries.associateBy { it.properties.id }

    PageSection(title = "This week", headerTextColor = MaterialTheme.colorScheme.onSurface) {
        LazyRow(
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = dateRange, key = { it }) { dayId ->
                when (val day = entries[dayId]) {
                    null -> ThisWeekAddEntryCard(
                        dayId = dayId,
                        onClick = { onAddEntry(dayId) },
                        modifier = Modifier.fillParentMaxWidth(CARD_WIDTH)
                    )
                    else -> ThisWeekCard(
                        day = day,
                        onClick = { onSelectEntry(dayId) },
                        modifier = Modifier.fillParentMaxWidth(CARD_WIDTH)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThisWeekCard(day: Day, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val date = LocalDate.ofEpochDay(day.properties.id)
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
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Utils.locale)
                        .uppercase(Utils.locale),
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
                    Text(text = day.tags.size.toString(), color = contentColor)
                    Icon(imageVector = Icons.Filled.BarChart, contentDescription = "Metrics")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThisWeekAddEntryCard(dayId: Long, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Utils.locale)
                        .uppercase(Utils.locale),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderThisWeekCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.fade()
            ),
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
                    text = ""
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "")
                }
            }
        }
    }
}