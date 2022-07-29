package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.SearchHeaderBar
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.days.DayListUiState
import me.ionice.snapshot.ui.days.DayListViewModel
import me.ionice.snapshot.ui.days.DaySearchQuery

@Composable
fun ListRoute(
    viewModel: DayListViewModel,
    onSelectDay: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        uiState = uiState,
        onSelectDay = onSelectDay,
        onAddDay = {
            viewModel.insertDay(it)
            onSelectDay(it)
        },
        onChangeYear = viewModel::switchYear,
        onSearchBarStateChange = {
            if (it) {
                viewModel.search(DaySearchQuery.initialize())
            } else {
                viewModel.clearSearch()
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListScreen(
    uiState: DayListUiState,
    onSelectDay: (Long) -> Unit,
    onAddDay: (Long) -> Unit,
    onChangeYear: (Int) -> Unit,
    onSearchBarStateChange: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()
    var expandedWeek by rememberSaveable { mutableStateOf(-1) }

    LazyColumn(state = scrollState) {
        stickyHeader {
            SearchBar(onSearchBarStateChange = onSearchBarStateChange)
        }

        when (uiState) {
            is DayListUiState.Loading -> {
                item {
                    LoadingScreen()
                }
            }
            is DayListUiState.Overview -> getOverviewScreen(
                listScope = this,
                daysInWeek = uiState.weekEntries,
                daysInYear = uiState.yearEntries,
                memories = uiState.memories,
                year = uiState.year,
                onSelectDay = onSelectDay,
                onAddDay = onAddDay,
                onChangeYear = {
                    expandedWeek = -1
                    onChangeYear(it)
                },
                expandedWeek = expandedWeek,
                setExpandedWeek = { expandedWeek = it }
            )
            is DayListUiState.Search -> getSearchScreen(
                listScope = this,
                query = uiState.query,
                onQueryChange = {}
            )
        }
    }
}

@Composable
private fun SearchBar(onSearchBarStateChange: (Boolean) -> Unit) {
    SearchHeaderBar(
        placeholderText = "Search entries",
        onSearchStringChange = {},
        onSearchBarActiveStateChange = onSearchBarStateChange,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.padding(12.dp)
            )
        },
    )
}



