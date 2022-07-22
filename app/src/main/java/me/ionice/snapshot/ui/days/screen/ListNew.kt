package me.ionice.snapshot.ui.days.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.SearchHeaderBar
import me.ionice.snapshot.ui.common.VerticalDivider
import me.ionice.snapshot.utils.FakeData
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewListScreen() {
    LazyColumn {
        stickyHeader {
            SearchBar()
        }

        item {
            CurrentWeek(emptyList(), onItemClick = {})
        }

        item {
            Memories()
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
private fun CurrentWeek(dayList: List<DayWithMetrics>, onItemClick: (Long) -> Unit) {
    if (dayList.size > 7) throw IllegalArgumentException("List of entries for the current week cannot exceed 7.")
    val currentDayId = LocalDate.now().toEpochDay()
    var currentDayExists = false
    dayList.forEach {
        if (it.day.id > currentDayId || currentDayId - 6 > it.day.id) {
            throw IllegalArgumentException("Entries passed in is not of the current week.")
        }
        if (it.day.id == currentDayId) {
            currentDayExists = true
        }
    }
    val reverseSortedDays =
        dayList.sortedWith { day1: DayWithMetrics, day2: DayWithMetrics -> -1 * (day1.day.id compareTo day2.day.id) }

    // TODO: use HorizontalPager from Accompanist instead
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        if (!currentDayExists) {
            // TODO: 'add' card for current day
        }
        reverseSortedDays.map {
            DayCard(day = it, onClick = { onItemClick(it.day.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayCard(day: DayWithMetrics, onClick: () -> Unit) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    Card(
        colors = cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(text = day.day.summary, color = contentColor)

            Divider(color = contentColor)

            DayCardInformation(
                date = LocalDate.ofEpochDay(day.day.id),
                textColor = contentColor,
                metricCount = day.metrics.size
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DayCardInformation(date: LocalDate, textColor: Color, metricCount: Int? = null) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CANADA),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        VerticalDivider(color = textColor)
        Text(
            text = date.format(Utils.shortDateFormatter),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        if (metricCount != null) {
            VerticalDivider(color = textColor)
            Text(
                text = pluralStringResource(
                    R.plurals.day_screen_metric_count,
                    metricCount,
                    metricCount
                ),
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}

@Composable
private fun Memories() {

}

@Composable
private fun DayGroupListHeader() {

}

@Composable
private fun DayGroupListItem() {

}

@Preview
@Composable
private fun CurrentWeekCardPreview() {
    DayCard(day = FakeData.longSummaryEntry) {

    }
}