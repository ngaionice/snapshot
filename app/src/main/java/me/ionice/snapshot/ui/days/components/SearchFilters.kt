package me.ionice.snapshot.ui.days.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.ColumnItem
import me.ionice.snapshot.ui.days.DayListUiState
import me.ionice.snapshot.ui.days.DaySearchQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilters(
    uiStateProvider: () -> DayListUiState,
    onDateClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    val uiState = uiStateProvider()

    if (uiState is DayListUiState.Search) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.query.dateRange !is DaySearchQuery.DateRange.Any,
                onClick = onDateClick,
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
                enabled = false,
                onClick = onLocationClick,
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
}

@Composable
fun SearchBottomSheetContent(
    uiStateProvider: () -> DayListUiState,
    contentType: BottomSheetContentType,
    setQuery: (DaySearchQuery) -> Unit,
    getLocations: suspend () -> List<String>
) {
    val uiState = uiStateProvider()
    if (uiState is DayListUiState.Search) {
        when (contentType) {
            BottomSheetContentType.DATE -> {
                SearchFilterDateOptions(
                    dateRange = uiState.query.dateRange,
                    setDateRange = { setQuery(uiState.query.copy(dateRange = it)) }
                )
            }
            BottomSheetContentType.LOCATION -> {
                LocationOptions(getLocations = getLocations)
            }
        }
    }
}

@Composable
private fun SearchFilterDateOptions(
    dateRange: DaySearchQuery.DateRange,
    setDateRange: (DaySearchQuery.DateRange) -> Unit
) {
    Column {
        SearchFilterDateOptionItem(
            selected = dateRange is DaySearchQuery.DateRange.Any,
            displayText = "Any time"
        ) {
            setDateRange(DaySearchQuery.DateRange.Any)
        }
        SearchFilterDateOptionItem(
            selected = dateRange is DaySearchQuery.DateRange.OneMonthPlus,
            displayText = "Older than a month"
        ) {
            setDateRange(DaySearchQuery.DateRange.OneMonthPlus)
        }
        SearchFilterDateOptionItem(
            selected = dateRange is DaySearchQuery.DateRange.ThreeMonthsPlus,
            displayText = "Older than 3 months"
        ) {
            setDateRange(DaySearchQuery.DateRange.ThreeMonthsPlus)
        }
        SearchFilterDateOptionItem(
            selected = dateRange is DaySearchQuery.DateRange.SixMonthsPlus,
            displayText = "Older than 6 months"
        ) {
            setDateRange(DaySearchQuery.DateRange.SixMonthsPlus)
        }
        SearchFilterDateOptionItem(
            selected = dateRange is DaySearchQuery.DateRange.OneYearPlus,
            displayText = "Older than a year"
        ) {
            setDateRange(DaySearchQuery.DateRange.OneYearPlus)
        }
        ColumnItem(onClick = {}) {
            Text("Custom range")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFilterDateOptionItem(
    selected: Boolean,
    displayText: String,
    onClick: () -> Unit
) {
    ColumnItem(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            RadioButton(selected = selected, onClick = null)
            Text(text = displayText)
        }
    }
}

@Composable
private fun LocationOptions(getLocations: suspend () -> List<String>) {
    // TODO: create list of checkboxes
}