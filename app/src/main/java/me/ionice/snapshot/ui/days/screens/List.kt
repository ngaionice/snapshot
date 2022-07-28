package me.ionice.snapshot.ui.days.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import me.ionice.snapshot.ui.days.DayListViewModel
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

@Composable
fun ListRoute(
    viewModel: DayListViewModel,
    onSelectDay: (Long) -> Unit,
    onStartSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        daysInWeek = uiState.weekEntries,
        daysInYear = uiState.yearEntries,
        memories = uiState.memories,
        year = uiState.year,
        onSelectDay = onSelectDay,
        onAddDay = {
            viewModel.insertDay(it)
            onSelectDay(it)
        },
        onChangeYear = viewModel::switchYear,
        onStartSearch = onStartSearch
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListScreen(
    daysInWeek: List<DayWithMetrics>,
    daysInYear: List<DayWithMetrics>,
    memories: List<DayWithMetrics>,
    year: Int,
    onSelectDay: (Long) -> Unit,
    onAddDay: (Long) -> Unit,
    onChangeYear: (Int) -> Unit,
    onStartSearch: () -> Unit
) {
    val scrollState = rememberLazyListState()
    var expandedWeek by rememberSaveable { mutableStateOf(-1) }

    LazyColumn(state = scrollState) {
        stickyHeader {
            SearchBar(onClick = onStartSearch)
        }

        item {
            CurrentWeek(days = daysInWeek, onSelectDay = onSelectDay, onAddDay = onAddDay)
        }

        if (memories.isNotEmpty()) {
            item {
                Memories(days = memories, onSelectDay = onSelectDay)
            }
        }

        stickyHeader {
            WeekListHeader(year = year, onChangeYear = onChangeYear)
        }

        getWeekList(
            daysInYear = daysInYear,
            onSelectDay = onSelectDay,
            listScope = this,
            expandedWeek = expandedWeek,
            setExpandedWeek = { expandedWeek = it })
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    SearchHeaderBar(
        placeholderText = "Search entries",
        onSearchStringChange = {},
        onSearchBarActiveStateChange = {
            onClick()
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
    onSelectDay: (Long) -> Unit,
    onAddDay: (Long) -> Unit
) {
    val currentDay = LocalDate.now()
    val currentDayId = currentDay.toEpochDay()
    val dayOffset = currentDay.dayOfWeek.value - 1
    val currentWeek: List<Long> = ((currentDayId - dayOffset)..currentDayId).toList().reversed()

    val map = mutableMapOf<Long, DayWithMetrics>()

    days.forEach {
        map[it.core.id] = it
    }

    PageSection(title = "This week", headerTextColor = MaterialTheme.colorScheme.onSurface) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = currentWeek, key = { dayId -> dayId }) { dayId ->
                val day = map[dayId]
                if (day == null) {
                    SmallAddDayCard(
                        dayId = dayId,
                        onClick = { onAddDay(dayId) },
                        modifier = Modifier.fillParentMaxWidth(0.3333f)
                    )
                } else {
                    SmallDayCard(
                        day = day,
                        onClick = { onSelectDay(dayId) },
                        modifier = Modifier.fillParentMaxWidth(0.3333f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Memories(days: List<DayWithMetrics>, onSelectDay: (Long) -> Unit) {
    PageSection(title = "Memories", headerTextColor = MaterialTheme.colorScheme.onSurface) {
        HorizontalPager(
            count = days.size,
            contentPadding = PaddingValues(horizontal = 24.dp),
            itemSpacing = 12.dp
        ) { page ->
            val day = days[page]
            val date = LocalDate.ofEpochDay(day.core.id)
            val relativeDate = RelativeTime.getPastDuration(date)
            LargeDayCard(
                day = day,
                onClick = { onSelectDay(day.core.id) },
                modifier = Modifier.fillMaxWidth()
            ) { color ->
                LargeDayCardInformation(
                    date = date,
                    textColor = color,
                    relativeDate = relativeDate,
                    location = day.core.location
                )
            }
        }
    }
}

@Composable
private fun WeekListHeader(year: Int, onChangeYear: (Int) -> Unit) {
    PageSectionHeader(title = year.toString(), textColor = MaterialTheme.colorScheme.onSurface, backgroundColor = MaterialTheme.colorScheme.surface) {
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
private fun getWeekList(
    daysInYear: List<DayWithMetrics>,
    onSelectDay: (Long) -> Unit,
    listScope: LazyListScope,
    expandedWeek: Int,
    setExpandedWeek: (Int) -> Unit
) {
    val weekMap: MutableMap<Int, MutableList<DayWithMetrics>> = mutableMapOf()
    val maxWeek = ZonedDateTime.now(zoneId).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

    daysInYear.forEach { day ->
        val date = LocalDate.ofEpochDay(day.core.id)
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

    (maxWeek downTo 0).forEach { week ->
        weekMap[week]?.let {
            listScope.item {
                WeekListItem(
                    days = it,
                    week = week,
                    isExpanded = expandedWeek == week,
                    onClickWeek = {
                        setExpandedWeek(
                            if (expandedWeek == week) {
                                -1
                            } else {
                                week
                            }
                        )
                    },
                    onViewItem = onSelectDay
                )
            }
        }
    }

    listScope.item {
        PageSectionContent {}
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
    val weekStart = LocalDate.ofEpochDay(days.last().core.id)
        .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val weekEnd =
        LocalDate.ofEpochDay(days.first().core.id).with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

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
                    WeekListSubItem(day = it, onViewItem = { onViewItem(it.core.id) })
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
        .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = Utils.shortDateFormatter.format(LocalDate.ofEpochDay(day.core.id)),
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
    CurrentWeek(days = emptyList(), onSelectDay = {}, onAddDay = {})
}

@Preview
@Composable
fun MemoriesPreview() {
    Memories(days = FakeData.varyingDateEntries, onSelectDay = {})
}

@Preview
@Composable
fun WeekListPreview() {
    var expandedWeek by rememberSaveable { mutableStateOf(-1) }
    Column {
        WeekListHeader(year = 2022, onChangeYear = {})
        LazyColumn {
            getWeekList(
                daysInYear = FakeData.varyingDateEntries.subList(0, 4),
                onSelectDay = {},
                listScope = this,
                expandedWeek = expandedWeek,
                setExpandedWeek = { expandedWeek = it })
        }
    }
}

