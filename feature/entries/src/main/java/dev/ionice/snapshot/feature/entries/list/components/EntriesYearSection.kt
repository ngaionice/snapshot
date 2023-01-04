package dev.ionice.snapshot.feature.entries.list.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.components.PageSectionContent
import dev.ionice.snapshot.core.ui.components.PageSectionHeader
import dev.ionice.snapshot.core.ui.components.PlaceholderText
import dev.ionice.snapshot.core.ui.components.VerticalDivider
import dev.ionice.snapshot.feature.entries.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

@Composable
internal fun YearSectionHeader(yearProvider: () -> Int, onChangeYear: (Int) -> Unit) {
    val year = yearProvider()
    val ttPrev = stringResource(R.string.tt_year_header_prev)
    val ttNext = stringResource(R.string.tt_year_header_next)
    PageSectionHeader(
        title = year.toString(),
        textColor = MaterialTheme.colorScheme.onSurface,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { onChangeYear(year - 1) },
                modifier = Modifier.semantics { testTag = ttPrev }) {
                Icon(
                    imageVector = Icons.Filled.NavigateBefore, contentDescription = "Previous year"
                )
            }
            IconButton(
                onClick = { onChangeYear(year + 1) },
                enabled = year < LocalDate.now().year,
                modifier = Modifier.semantics { testTag = ttNext }
            ) {
                Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Next year")
            }
        }
    }
}

fun LazyListScope.getYearSectionContent(
    uiStateProvider: () -> DaysUiState,
    yearProvider: () -> Int,
    expandedWeek: Int,
    setExpandedWeek: (Int) -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    when (val uiState = uiStateProvider()) {
        is DaysUiState.Loading -> {
            items(count = 10) {
                YearSectionItemPlaceholder()
            }
        }
        is DaysUiState.Error -> {
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
        }
        is DaysUiState.Success -> {
            val year = yearProvider()
            val entries = uiState.data

            if (entries.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.year_no_results))
                    }
                }
                return
            }

            val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 7)

            val map = entries.groupBy { day ->
                val date = LocalDate.ofEpochDay(day.id)
                date.get(weekFields.weekOfWeekBasedYear())
                    .let { if (it == 53 && date.dayOfYear < 7) 0 else it }
            }

            val today = LocalDate.now()
            val maxWeek = if (today.year > year) LocalDate.ofYearDay(year, 1)
                .range(weekFields.weekOfWeekBasedYear()).maximum.toInt() else today.get(weekFields.weekOfWeekBasedYear())
            (maxWeek downTo 0).forEach { week ->
                map[week]?.let {
                    item {
                        YearSectionItem(
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearSectionItem(
    days: List<Day>,
    week: Int,
    isExpanded: Boolean,
    onSelectWeek: () -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    val startOfWeek = LocalDate.ofEpochDay(days.last().id)
        .with(TemporalAdjusters.previousOrSame(Utils.firstDayOfWeek))
    val endOfWeek = LocalDate.ofEpochDay(days.last().id)
        .with(TemporalAdjusters.nextOrSame(Utils.lastDayOfWeek))
    val tt = stringResource(R.string.tt_year_item)

    Column {
        ListItem(
            modifier = Modifier
                .clickable { onSelectWeek() }
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .semantics { testTag = tt },
            headlineText = {
                Text(
                    text = Utils.shortDateFormatter.format(startOfWeek) + " - " + Utils.shortDateFormatter.format(
                        endOfWeek
                    )
                )
            },
            overlineText = {
                Text(text = "Week $week")
            })
        AnimatedVisibility(visible = isExpanded) {
            Column {
                days.forEach {
                    YearSectionSubItem(dayProvider = { it },
                        onViewItem = { onSelectEntry(it.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun YearSectionSubItem(dayProvider: () -> Day, onViewItem: () -> Unit) {
    val day = dayProvider()
    val tt = stringResource(R.string.tt_year_subitem)
    Row(modifier = Modifier
        .background(color = MaterialTheme.colorScheme.surface)
        .clickable { onViewItem() }
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .semantics { testTag = tt },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = Utils.shortDateFormatter.format(LocalDate.ofEpochDay(day.id)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = pluralStringResource(
                R.plurals.tag_count, day.tags.size, day.tags.size
            ),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun YearSectionItemPlaceholder() {
    val tt = stringResource(R.string.tt_year_item_placeholder)
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .semantics { testTag = tt }) {
        PlaceholderText(textStyle = MaterialTheme.typography.labelSmall, Modifier.width(50.dp))
        PlaceholderText(textStyle = MaterialTheme.typography.titleMedium, Modifier.width(150.dp))
    }
}