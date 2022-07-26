package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.common.components.SearchHeaderBar
import me.ionice.snapshot.ui.days.components.LargeDayCard
import me.ionice.snapshot.ui.days.components.LargeDayCardInformation
import me.ionice.snapshot.ui.days.components.SmallAddDayCard
import me.ionice.snapshot.ui.days.components.SmallDayCard
import me.ionice.snapshot.utils.FakeData
import me.ionice.snapshot.utils.RelativeTime
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewListScreen() {
    LazyColumn {
        stickyHeader {
            SearchBar()
        }

        item {
            PageSection(title = "This week", headerColor = MaterialTheme.colorScheme.onSurface) {
                CurrentWeek(emptyList(), onViewItem = {}, onAddItem = {})
            }
        }

        item {
            PageSection(title = "Memories", headerColor = MaterialTheme.colorScheme.onSurface) {
                Memories(emptyList(), onViewItem = {})
            }
        }

        stickyHeader {
            DayGroupListHeader()
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

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
private fun Memories(days: List<DayWithMetrics>, onViewItem: (Long) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = days, key = { day -> day.day.id }) { day ->
            val date = LocalDate.ofEpochDay(day.day.id)
            val relativeDate = RelativeTime.getPastDuration(date)
            LargeDayCard(
                day = day,
                onClick = { onViewItem(day.day.id) },
                modifier = Modifier.fillParentMaxWidth(1f)
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
private fun DayGroupListHeader() {

}

@Composable
private fun DayGroupListItem() {

}

@Preview
@Composable
fun CurrentWeekPreview() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
    PageSection(title = "Memories", headerColor = MaterialTheme.colorScheme.onSurface) {
        Memories(days = FakeData.varyingDateEntries, onViewItem = {})
    }
}
