package me.ionice.snapshot.ui.days.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import me.ionice.snapshot.R
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.components.*
import me.ionice.snapshot.ui.days.components.LargeDayCard
import me.ionice.snapshot.ui.days.components.LargeDayCardInformation
import me.ionice.snapshot.ui.days.components.SmallAddDayCard
import me.ionice.snapshot.ui.days.components.SmallDayCard
import me.ionice.snapshot.utils.FakeData
import me.ionice.snapshot.utils.RelativeTime
import me.ionice.snapshot.utils.Utils
import me.ionice.snapshot.utils.Utils.firstDayOfWeek
import me.ionice.snapshot.utils.Utils.lastDayOfWeek
import me.ionice.snapshot.utils.Utils.zoneId
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewListScreen() {
    LazyColumn {
        stickyHeader {
            SearchBar()
        }

        item {
            CurrentWeek(emptyList(), onViewItem = {}, onAddItem = {})
        }

        item {
            Memories(emptyList(), onViewItem = {})
        }

        stickyHeader {
            WeekListHeader(LocalDate.now().year, onChangeYear = {})
        }

        item {

        }
    }
}

@Composable
private fun SearchBar() {
    SearchHeaderBar(
        placeholderText = "Search entries",
        onSearchStringChange = {

        },
        onSearchBarActiveStateChange = {
            // TODO: change system bar colors
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.padding(12.dp)
            )
        },
    )
}

@Composable
private fun CurrentWeek(
    days: List<DayWithMetrics>,
    onViewItem: (Long) -> Unit,
    onAddItem: (Long) -> Unit
) {
    val currentDay = LocalDate.now()
    val currentDayId = currentDay.toEpochDay()
    val dayOffset = currentDay.dayOfWeek.value - 1
    val currentWeek: List<Long> = ((currentDayId - dayOffset)..currentDayId).toList()

    val map = mutableMapOf<Long, DayWithMetrics>()

    days.forEach {
        map[it.day.id] = it
    }

    PageSection(title = "This week", headerColor = MaterialTheme.colorScheme.onSurface) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = currentWeek, key = { dayId -> dayId }) { dayId ->
                val day = map[dayId]
                if (day == null) {
                    SmallAddDayCard(
                        dayId = dayId,
                        onClick = { onAddItem(dayId) },
                        modifier = Modifier.fillParentMaxWidth(0.3333f)
                    )
                } else {
                    SmallDayCard(
                        day = day,
                        onClick = { onViewItem(dayId) },
                        modifier = Modifier.fillParentMaxWidth(0.3333f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Memories(days: List<DayWithMetrics>, onViewItem: (Long) -> Unit) {
    PageSection(title = "Memories", headerColor = MaterialTheme.colorScheme.onSurface) {
        HorizontalPager(
            count = days.size,
            contentPadding = PaddingValues(horizontal = 24.dp),
            itemSpacing = 12.dp
        ) { page ->
            val day = days[page]
            val date = LocalDate.ofEpochDay(day.day.id)
            val relativeDate = RelativeTime.getPastDuration(date)
            LargeDayCard(
                day = day,
                onClick = { onViewItem(day.day.id) },
                modifier = Modifier.fillMaxWidth()
            ) { color ->
                LargeDayCardInformation(
                    date = date,
                    textColor = color,
                    relativeDate = relativeDate,
                    location = day.day.location
                )
            }
        }
    }
}

@Composable
private fun WeekListHeader(year: Int, onChangeYear: (Int) -> Unit) {
    PageSectionHeader(title = year.toString()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onChangeYear(year - 1) }) {
                Icon(
                    imageVector = Icons.Filled.NavigateBefore,
                    contentDescription = "Previous year"
                )
            }
            IconButton(
                onClick = { onChangeYear(year + 1) },
                enabled = year < LocalDate.now().year
            ) {
                Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = "Next year")
            }
        }
    }
}

/**
 * @param daysInYear A list of DayWithMetrics, where all entries are in the same year,
 * sorted by the ID of the day attribute in each object, where the largest value comes first.
 */
@Composable
private fun WeekList(daysInYear: List<DayWithMetrics>, onViewItem: (Long) -> Unit) {
    val weekMap: MutableMap<Int, MutableList<DayWithMetrics>> = mutableMapOf()
    val maxWeek = ZonedDateTime.now(zoneId).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

    var expandedWeek by rememberSaveable { mutableStateOf(-1) }

    daysInYear.forEach { day ->
        val date = LocalDate.ofEpochDay(day.day.id)
        var week = ZonedDateTime.of(date, LocalTime.of(0, 0), zoneId)
            .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        if (week == 53 && date.dayOfYear < 7) {
            week = 0
        }
        if (weekMap[week] != null) {
            weekMap[week]!!.add(day)
        } else {
            weekMap[week] = mutableListOf(day)
        }
    }

    PageSectionContent {
        LazyColumn {
            (maxWeek downTo 0).forEach { week ->
                weekMap[week]?.let {
                    item {
                        WeekListItem(
                            days = it,
                            week = week,
                            isExpanded = expandedWeek == week,
                            onClickWeek = {
                                expandedWeek = if (expandedWeek == week) {
                                    -1
                                } else {
                                    week
                                }
                            },
                            onViewItem = onViewItem
                        )
                    }
                }
            }
        }
    }
}

/**
 * @param days Sorted list of DayWithMetrics, where the latest date is first in the list.
 */
@Composable
private fun WeekListItem(
    days: List<DayWithMetrics>,
    week: Int,
    isExpanded: Boolean,
    onClickWeek: () -> Unit,
    onViewItem: (Long) -> Unit
) {
    val weekStart = LocalDate.ofEpochDay(days.last().day.id)
        .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val weekEnd =
        LocalDate.ofEpochDay(days.first().day.id).with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

    Column {
        Row(modifier = Modifier
            .clickable { onClickWeek() }
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column {
                Text(
                    text = "Week $week",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                )
                Text(
                    text = "${Utils.shortDateFormatter.format(weekStart)} - ${
                        Utils.shortDateFormatter.format(
                            weekEnd
                        )
                    }",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            Column {
                days.reversed().forEach {
                    WeekListSubItem(day = it, onViewItem = { onViewItem(it.day.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun WeekListSubItem(day: DayWithMetrics, onViewItem: () -> Unit) {
    Row(modifier = Modifier
        .background(color = MaterialTheme.colorScheme.surface)
        .clickable { onViewItem() }
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = Utils.shortDateFormatter.format(LocalDate.ofEpochDay(day.day.id)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = pluralStringResource(
                R.plurals.day_screen_metric_count,
                day.metrics.size,
                day.metrics.size
            ), style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun CurrentWeekPreview() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SmallAddDayCard(
                dayId = 10000, onClick = {}, modifier = Modifier
                    .fillParentMaxWidth(0.3333f)
            )
        }

        items(7) {
            SmallDayCard(
                day = FakeData.longSummaryEntry, onClick = {}, modifier = Modifier
                    .fillParentMaxWidth(0.3333f)
            )
        }
    }
}

@Preview
@Composable
fun MemoriesPreview() {
    Memories(days = FakeData.varyingDateEntries, onViewItem = {})
}

@Preview
@Composable
fun WeekListPreview() {
    Column {
        WeekListHeader(year = 2022, onChangeYear = {})
        WeekList(daysInYear = FakeData.varyingDateEntries.subList(0, 4), onViewItem = {})
    }
}

