package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
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
            CurrentWeek(emptyList(), onViewItem = {}, onAddItem = {})
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
private fun CurrentWeek(
    dayList: List<DayWithMetrics>,
    onViewItem: (Long) -> Unit,
    onAddItem: (Long) -> Unit
) {
    val currentDay = LocalDate.now()
    val currentDayId = currentDay.toEpochDay()
    val dayOffset = currentDay.dayOfWeek.value - 1
    val currentWeek: List<Long> = ((currentDayId - dayOffset)..currentDayId).toList()

    val map = mutableMapOf<Long, DayWithMetrics>()

    dayList.forEach {
        map[it.day.id] = it
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = currentWeek, key = { dayId -> dayId }) { dayId ->
            val day = map[dayId]
            if (day == null) {
                SmallAddDayCard(
                    dayId = dayId,
                    onClick = { onAddItem(dayId) },
                    modifier = Modifier.fillParentMaxWidth(0.3f)
                )
            } else {
                SmallDayCard(
                    day = day,
                    onClick = { onViewItem(dayId) },
                    modifier = Modifier.fillParentMaxWidth(0.3f)
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallDayCard(day: DayWithMetrics, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val date = LocalDate.ofEpochDay(day.day.id)
    Card(
        modifier = modifier,
        colors = cardColors(containerColor = containerColor),
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
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
                        .uppercase(Locale.US),
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
private fun SmallAddDayCard(dayId: Long, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val date = LocalDate.ofEpochDay(dayId)
    Card(
        modifier = modifier,
        colors = cardColors(containerColor = containerColor),
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
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
                        .uppercase(Locale.US),
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
private fun LargeDayCard(day: DayWithMetrics, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    Card(
        modifier = modifier,
        colors = cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text(text = day.day.summary, color = contentColor)

            Divider(color = contentColor)

            LargeDayCardInformation(
                date = LocalDate.ofEpochDay(day.day.id),
                textColor = contentColor,
                metricCount = day.metrics.size
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LargeDayCardInformation(
    date: LocalDate,
    textColor: Color,
    location: String? = null,
    metricCount: Int? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        VerticalDivider(color = textColor)
        Text(
            text = date.format(Utils.shortDateFormatter),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
        if (location != null) {
            VerticalDivider(color = textColor)
            Text(
                text = location,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
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
fun CurrentWeekPreview() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SmallAddDayCard(
                dayId = 10000, onClick = {}, modifier = Modifier
                    .fillParentMaxWidth(0.3f)
            )
        }

        items(7) {
            SmallDayCard(
                day = FakeData.longSummaryEntry, onClick = {}, modifier = Modifier
                    .fillParentMaxWidth(0.3f)
            )
        }
    }
}

@Preview
@Composable
private fun LargeDayCardPreview() {
    LargeDayCard(day = FakeData.longSummaryEntry, onClick = {})
}