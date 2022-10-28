//package me.ionice.snapshot.ui.days.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.History
//import androidx.compose.material.icons.filled.ManageSearch
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import dev.ionice.DayProperties
//import me.ionice.snapshot.ui.common.components.ColumnItem
//import dev.ionice.PageSection
//import me.ionice.snapshot.ui.days.DayListUiState
//import me.ionice.snapshot.ui.days.DaySearchQuery
//import dev.ionice.Utils
//import java.time.LocalDate
//
//@Composable
//fun SearchOptionsScreen(
//    contentPadding: PaddingValues,
//    uiState: DayListUiState.Search.Options,
//    onSearch: (DaySearchQuery) -> Unit,
//    onSelectDayFromQuickResults: (Long) -> Unit
//) {
//
//    val (query, quickResults) = uiState
//    val (searchTerm) = query
//
//    Column(modifier = Modifier.padding(contentPadding)) {
//        if (searchTerm.isEmpty()) {
//            SearchHistory(recentSearches = listOf("snapshot", "work", "swimming"), onSearch = {})
//        } else {
//            SearchButton(searchTerm = searchTerm, onSearch = { onSearch(query) })
//
//            QuickResults(
//                searchTerm = searchTerm,
//                results = quickResults,
//                onSelectDay = onSelectDayFromQuickResults
//            )
//        }
//    }
//}
//
//@Composable
//fun SearchResultsScreen(
//    contentPadding: PaddingValues,
//    searchTerm: String,
//    results: List<DayProperties>,
//    onSelectDay: (Long) -> Unit
//) {
//    if (results.isEmpty()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(contentPadding),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text("No results found.")
//        }
//        return
//    }
//
//    val currentYear = LocalDate.now().year
//
//    PageSection(
//        title = "Results",
//        headerTextColor = MaterialTheme.colorScheme.onSurface,
//        modifier = Modifier.padding(contentPadding)
//    ) {
//        LazyColumn {
//            results.forEach {
//                item {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { onSelectDay(it.id) }
//                            .padding(horizontal = 24.dp, vertical = 16.dp)
//                    ) {
//                        val date = LocalDate.ofEpochDay(it.id)
//                        Text(
//                            text = date.format(
//                                if (date.year == currentYear) {
//                                    Utils.shortDateFormatter
//                                } else {
//                                    Utils.dateFormatter
//                                }
//                            ),
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                        Text(
//                            text = getSearchResultDisplayText(searchTerm, it.summary),
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SearchHistory(recentSearches: List<String>, onSearch: (String) -> Unit) {
//    PageSection(title = "Recent searches", headerTextColor = MaterialTheme.colorScheme.onSurface) {
//        Column {
//            recentSearches.forEach {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { onSearch(it) }
//                        .padding(horizontal = 24.dp, vertical = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(24.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.History,
//                        contentDescription = "Recent search: $it"
//                    )
//                    Text(text = it)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SearchButton(searchTerm: String, onSearch: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onSearch)
//            .padding(horizontal = 24.dp, vertical = 16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(24.dp)
//    ) {
//        Icon(
//            imageVector = Icons.Filled.ManageSearch,
//            contentDescription = "Search"
//        )
//        Text(text = "Search $searchTerm in entries")
//    }
//}
//
//@Composable
//private fun QuickResults(
//    searchTerm: String,
//    results: List<DayProperties>,
//    onSelectDay: (Long) -> Unit
//) {
//    if (results.isEmpty()) return
//
//    val currentYear = LocalDate.now().year
//
//    PageSection(title = "Quick results", headerTextColor = MaterialTheme.colorScheme.onSurface) {
//        Column {
//            results.forEach {
//                val date = LocalDate.ofEpochDay(it.id)
//                ColumnItem(onClick = { onSelectDay(it.id) }) {
//                    Text(
//                        text = date.format(
//                            if (date.year == currentYear) {
//                                Utils.shortDateFormatter
//                            } else {
//                                Utils.dateFormatter
//                            }
//                        ),
//                        style = MaterialTheme.typography.labelMedium
//                    )
//                    Text(
//                        text = getSearchResultDisplayText(searchTerm, it.summary),
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 1
//                    )
//                }
//            }
//        }
//    }
//}
//
///**
// * @param summary The summary to be extracted for display text. If it does not contain [searchTerm], an empty string will be returned.
// */
//private fun getSearchResultDisplayText(searchTerm: String, summary: String): String {
//
//    val targetIdx = summary.indexOf(string = searchTerm, ignoreCase = true)
//    if (targetIdx == -1) return ""
//
//    // Get the index of the 2nd last word before the search term
//    // Why 2nd last word? Copied from Gmail, they probably determined w/ research that the 2nd last word provides enough context
//    val get2ndLastWordStartIdx: (String, Int) -> Int = { string, startIdx ->
//        var count = 0
//        var idx = startIdx
//
//        while (idx > 0 && count < 3) {
//            if (string[idx].isWhitespace()) {
//                count += 1
//                if (count == 3) break
//            }
//            idx -= 1
//        }
//
//        if (idx != 0) idx + 1 else 0
//    }
//
//    return summary.substring(get2ndLastWordStartIdx(summary, targetIdx))
//}
//
//@Preview
//@Composable
//private fun SearchHistoryPreview() {
//    SearchHistory(recentSearches = listOf("snapshot", "work", "swimming"), onSearch = {})
//}