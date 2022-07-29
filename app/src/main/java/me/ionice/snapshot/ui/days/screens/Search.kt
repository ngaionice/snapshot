package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.days.DaySearchQuery
import java.time.LocalDate


fun getSearchScreen(
    listScope: LazyListScope,
    query: DaySearchQuery,
    onQueryChange: (DaySearchQuery) -> Unit
) {

    listScope.item {
        SearchFilters(query = query, onYearRangeChange = {}, onLocationsChange = {})
    }

    if (query.searchString == "") {
        listScope.item {
            SearchHistory()
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
    Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        FilterChip(
            selected = query.dateRange.first.compareTo(LocalDate.MIN) != 0 ||
                    query.dateRange.second.compareTo(LocalDate.MAX) != 0,
            onClick = { },
            label = { Text("Date range") }
        )
    }
}

@Composable
private fun SearchHistory() {

}

@Composable
private fun QuickResults() {

}

@Composable
private fun FullResults() {

}
