package me.ionice.snapshot.ui.entries.list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import me.ionice.snapshot.R
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.components.PageSectionContent
import me.ionice.snapshot.ui.common.components.PageSectionHeader
import me.ionice.snapshot.ui.common.components.VerticalDivider
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

@Composable
fun YearListHeader(yearProvider: () -> Int, onChangeYear: (Int) -> Unit) {
    val year = yearProvider()
    PageSectionHeader(
        title = year.toString(),
        textColor = MaterialTheme.colorScheme.onSurface,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onChangeYear(year - 1) }) {
                Icon(
                    imageVector = Icons.Filled.NavigateBefore, contentDescription = "Previous year"
                )
            }
            IconButton(
                onClick = { onChangeYear(year + 1) }, enabled = year < LocalDate.now().year
            ) {
                Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Next year")
            }
        }
    }
}

fun LazyListScope.getYearList(
    uiStateProvider: () -> DaysUiState,
    expandedWeek: Int,
    setExpandedWeek: (Int) -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    val uiState = uiStateProvider()

    if (uiState is DaysUiState.Loading) {
        items(7) {
            PlaceholderYearListItem()
        }
        return
    }

    if (uiState is DaysUiState.Error) {
        item {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load data for the selected year.")
            }
        }
        return
    }

    val entries = (uiState as DaysUiState.Success).data

    if (entries.isEmpty()) {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text("No entries found.")
            }
        }
        return
    }

    val weekFields = WeekFields.of(Utils.locale)

    val map = entries.groupBy { day ->
        val date = LocalDate.ofEpochDay(day.properties.id)
        date.get(weekFields.weekOfWeekBasedYear())
            .let { if (it == 53 && date.dayOfYear < 7) 0 else it }
    }

    val maxWeek = LocalDate.now().get(weekFields.weekOfWeekBasedYear())
    (maxWeek downTo 0).forEach { week ->
        map[week]?.let {
            item {
                YearListItem(
                    days = it,
                    week = week,
                    isExpanded = expandedWeek == week,
                    onSelectWeek = { setExpandedWeek(if (expandedWeek == week) -1 else week) },
                    onSelectEntry = onSelectEntry
                )
            }
        }
    }

    // empty spacer for consistency
    item {
        PageSectionContent {}
    }
}

@Composable
private fun YearListItem(
    days: List<Day>,
    week: Int,
    isExpanded: Boolean,
    onSelectWeek: () -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    val startOfWeek = LocalDate.ofEpochDay(days.last().properties.id)
        .with(TemporalAdjusters.previousOrSame(Utils.firstDayOfWeek))
    val endOfWeek = LocalDate.ofEpochDay(days.last().properties.id)
        .with(TemporalAdjusters.nextOrSame(Utils.lastDayOfWeek))

    Column {
        Row(modifier = Modifier
            .clickable { onSelectWeek() }
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column {
                Text(
                    text = "Week $week",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                )
                Text(
                    text = Utils.shortDateFormatter.format(startOfWeek) + " - " + Utils.shortDateFormatter.format(
                        endOfWeek
                    ), style = MaterialTheme.typography.titleMedium
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            Column {
                days.forEach {
                    YearListSubItem(dayProvider = { it },
                        onViewItem = { onSelectEntry(it.properties.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun YearListSubItem(dayProvider: () -> Day, onViewItem: () -> Unit) {
    val day = dayProvider()
    Row(modifier = Modifier
        .background(color = MaterialTheme.colorScheme.surface)
        .clickable { onViewItem() }
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = Utils.shortDateFormatter.format(LocalDate.ofEpochDay(day.properties.id)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = pluralStringResource(
                R.plurals.day_screen_tag_count, day.tags.size, day.tags.size
            ),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PlaceholderYearListItem() {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(40.dp)
            .placeholder(visible = true, highlight = PlaceholderHighlight.fade())
    ) {

    }
}