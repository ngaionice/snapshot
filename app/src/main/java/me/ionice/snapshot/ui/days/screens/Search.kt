package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.days.DayListUiState
import me.ionice.snapshot.ui.days.DaySearchQuery
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate


fun getSearchScreen(
    listScope: LazyListScope,
    uiState: DayListUiState.Search,
    onQueryChange: (DaySearchQuery) -> Unit,
    onSearch: (DaySearchQuery) -> Unit,
    onSelectDayFromQuickResults: (Long) -> Unit
) {
    val (query, quickResults) = uiState
    val (_, searchString) = query

    listScope.item {
        SearchFilters(query = query, onYearRangeChange = {}, onLocationsChange = {})
    }

    if (searchString.isEmpty()) {
        listScope.item {
            SearchHistory(recentSearches = listOf("snapshot", "work", "swimming"), onSearch = {})
        }
    } else {
        if (searchString.length >= 3) {
            listScope.item {
                SearchButton(queryString = searchString, onSearch = { onSearch(query) })
            }
        }
        listScope.item {
            QuickResults(
                queryString = searchString,
                results = quickResults,
                onSelectDay = onSelectDayFromQuickResults
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFilters(
    query: DaySearchQuery,
    onYearRangeChange: (Pair<LocalDate, LocalDate>) -> Unit,
    onLocationsChange: (List<String>) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = query.dateRange.first.compareTo(LocalDate.MIN) != 0 ||
                    query.dateRange.second.compareTo(LocalDate.MAX) != 0,
            onClick = { },
            label = { Text("Date") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Filter by date range"
                )
            }
        )

        FilterChip(
            selected = false,
            onClick = { },
            label = { Text("Location") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Filter by location"
                )
            }
        )
    }
}

@Composable
private fun SearchHistory(recentSearches: List<String>, onSearch: (String) -> Unit) {
    PageSection(title = "Recent searches", headerTextColor = MaterialTheme.colorScheme.onSurface) {
        Column {
            recentSearches.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSearch(it) }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Recent search: $it"
                    )
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
private fun SearchButton(queryString: String, onSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSearch)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ManageSearch,
            contentDescription = "Search"
        )
        Text(text = "Search $queryString in entries")
    }
}

@Composable
private fun QuickResults(
    queryString: String,
    results: List<DayWithMetrics>,
    onSelectDay: (Long) -> Unit
) {
    if (results.isEmpty()) return

    val currentYear = LocalDate.now().year

    PageSection(title = "Quick results", headerTextColor = MaterialTheme.colorScheme.onSurface) {
        Column {
            results.forEach {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDay(it.core.id) }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    val date = LocalDate.ofEpochDay(it.core.id)
                    Text(
                        text = date.format(
                            if (date.year == currentYear) {
                                Utils.shortDateFormatter
                            } else {
                                Utils.dateFormatter
                            }
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = getSearchResultDisplayText(queryString, it.core.summary),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * @param summary The summary to be extracted for display text. If it does not contain [queryString], an empty string will be returned.
 */
private fun getSearchResultDisplayText(queryString: String, summary: String): String {

    val targetIdx = summary.indexOf(string = queryString, ignoreCase = true)
    if (targetIdx == -1) return ""

    // Get the index of the 2nd last word before the query string
    // Why 2nd last word? Copied from Gmail, they probably determined w/ research that the 2nd last word provides enough context
    val get2ndLastWordStartIdx: (String, Int) -> Int = { string, startIdx ->
        var count = 0
        var idx = startIdx

        while (idx > 0 && count < 3) {
            if (string[idx].isWhitespace()) {
                count += 1
                if (count == 3) break
            }
            idx -= 1
        }

        if (idx != 0) idx + 1 else 0
    }

    return summary.substring(get2ndLastWordStartIdx(summary, targetIdx))
}

@Composable
private fun FullResults() {

}

@Preview
@Composable
private fun SearchHistoryPreview() {
    SearchHistory(recentSearches = listOf("snapshot", "work", "swimming"), onSearch = {})
}