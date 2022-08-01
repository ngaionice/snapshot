package me.ionice.snapshot.ui.days.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.SearchBarState
import me.ionice.snapshot.ui.common.components.SearchHeaderBar
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.days.DayListUiState
import me.ionice.snapshot.ui.days.DayListViewModel
import me.ionice.snapshot.ui.days.DaySearchQuery
import kotlin.math.roundToInt

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
        onYearChange = viewModel::switchYear,
        setIsSearching = {
            if (it) {
                viewModel.setQuery(DaySearchQuery.initialize())
            } else {
                viewModel.clearSearch()
            }
        },
        onSearchQueryChange = viewModel::setQuery
    )
}

@Composable
private fun ListScreen(
    uiState: DayListUiState,
    onSelectDay: (Long) -> Unit,
    onAddDay: (Long) -> Unit,
    onYearChange: (Int) -> Unit,
    setIsSearching: (Boolean) -> Unit,
    onSearchQueryChange: (DaySearchQuery) -> Unit
) {
    var expandedWeek by rememberSaveable { mutableStateOf(-1) }
    var searchBarState by rememberSaveable { mutableStateOf(SearchBarState.NOT_SEARCHING) }
    var searchTerm by rememberSaveable { mutableStateOf("") }

    val searchBarHeight = 64.dp
    val searchBarHeightPx = with(LocalDensity.current) { searchBarHeight.roundToPx().toFloat() }
    val searchBarOffsetHeightPx = remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = searchBarOffsetHeightPx.value + delta
                searchBarOffsetHeightPx.value = newOffset.coerceIn(-searchBarHeightPx, 0f)

                return Offset.Zero
            }
        }
    }

    val contentPadding = PaddingValues(top = searchBarHeight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column {
            when (uiState) {
                is DayListUiState.Loading -> {
                    LoadingScreen()
                }
                is DayListUiState.Overview -> OverviewScreen(
                    contentPadding = contentPadding,
                    daysInWeek = uiState.weekEntries,
                    daysInYear = uiState.yearEntries,
                    memories = uiState.memories,
                    year = uiState.year,
                    onSelectDay = onSelectDay,
                    onAddDay = onAddDay,
                    onChangeYear = {
                        expandedWeek = -1
                        onYearChange(it)
                    },
                    expandedWeek = expandedWeek,
                    setExpandedWeek = { expandedWeek = it }
                )
                is DayListUiState.Search -> SearchScreen(
                    contentPadding = contentPadding,
                    uiState = uiState,
                    onQueryChange = onSearchQueryChange,
                    onSearch = {},
                    onSelectDayFromQuickResults = {
                        onSelectDay(it)
                        searchBarState = SearchBarState.NOT_SEARCHING
                    }
                )
            }
        }

        SearchBar(
            searchBarState = searchBarState,
            setSearchBarState = {
                searchBarState = it
                setIsSearching(it != SearchBarState.NOT_SEARCHING)
            },
            modifier = Modifier
                .height(searchBarHeight)
                .offset { IntOffset(x = 0, y = searchBarOffsetHeightPx.value.roundToInt()) },
            searchTerm = searchTerm,
            setSearchTerm = {
                searchTerm = it
                onSearchQueryChange((uiState as DayListUiState.Search).query.copy(searchTerm = it))
            }
        )
    }

    // reset search state and everything when search bar state becomes not searching
    LaunchedEffect(key1 = searchBarState) {
        if (searchBarState == SearchBarState.NOT_SEARCHING) {
            searchTerm = ""
            setIsSearching(false)
        }
    }

    // if a search has been initiated, cancel the search and reset to default
    BackHandler(enabled = searchBarState != SearchBarState.NOT_SEARCHING) {
        searchBarState = SearchBarState.NOT_SEARCHING
    }
}

@Composable
private fun SearchBar(
    searchTerm: String,
    setSearchTerm: (String) -> Unit,
    searchBarState: SearchBarState,
    setSearchBarState: (SearchBarState) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchHeaderBar(
        searchBarState = searchBarState,
        setSearchBarState = setSearchBarState,
        modifier = modifier,
        placeholderText = "Search entries",
        searchTerm = searchTerm,
        setSearchTerm = setSearchTerm,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier.padding(12.dp)
            )
        },
    )
}



