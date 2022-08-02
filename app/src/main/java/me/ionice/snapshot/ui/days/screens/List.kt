package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.ionice.snapshot.ui.common.components.SearchBarState
import me.ionice.snapshot.ui.days.DayListUiState
import me.ionice.snapshot.ui.days.DayListViewModel
import me.ionice.snapshot.ui.days.DayListViewModelState
import me.ionice.snapshot.ui.days.DaySearchQuery
import me.ionice.snapshot.ui.days.components.*

@Composable
fun ListRoute(
    viewModel: DayListViewModel,
    onSelectDay: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val onAddDay: (Long) -> Unit = {
        viewModel.insertDay(it)
        onSelectDay(it)
    }

    ListScreen(
        uiStateProvider = { uiState },
        setQuery = viewModel::setQuery,
        setScreenMode = viewModel::setScreenMode,
        onSelectDay = onSelectDay,
        onAddDay = onAddDay,
        onChangeYear = viewModel::switchYear,
        getLocations = { emptyList() } // TODO: update when available
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ListScreen(
    uiStateProvider: () -> DayListUiState,
    setQuery: (DaySearchQuery) -> Unit,
    setScreenMode: (DayListViewModelState.ScreenMode) -> Unit,
    onSelectDay: (Long) -> Unit,
    onAddDay: (Long) -> Unit,
    onChangeYear: (Int) -> Unit,
    getLocations: suspend () -> List<String>,
) {
    var searchBarState by remember { mutableStateOf(SearchBarState.NOT_SEARCHING) }

    var bottomSheetContentType by remember { mutableStateOf(BottomSheetContentType.DATE) }
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val topBarHeightProvider: () -> Dp = {
        if (uiStateProvider() is DayListUiState.Overview) 64.dp else 128.dp
    }

    val searchTerm: () -> String = {
        val uiState = uiStateProvider()
        if (uiState is DayListUiState.Search) uiState.query.searchTerm else ""
    }

    val setSearchTerm: (String) -> Unit = {
        val uiState = uiStateProvider()
        if (uiState is DayListUiState.Search.Options) {
            setQuery(uiState.query.copy(searchTerm = it))
        }
    }

    val setSearchBarState: (SearchBarState) -> Unit = {
        searchBarState = it
        setScreenMode(
            when (it) {
                SearchBarState.NOT_SEARCHING -> DayListViewModelState.ScreenMode.OVERVIEW
                SearchBarState.ACTIVE -> DayListViewModelState.ScreenMode.SEARCH_OPTIONS
                SearchBarState.INACTIVE -> DayListViewModelState.ScreenMode.SEARCH_RESULTS
            }
        )
    }

    val onFilterChipClick: (BottomSheetContentType) -> Unit = {
        bottomSheetContentType = it
        focusRequester.requestFocus()
        scope.launch {
            bottomSheetState.show()
        }
    }

    ListScreenBase(
        topBarHeightProvider = topBarHeightProvider,
        scrollableTopBar = { heightOffset ->
            DaySearchBar(
                modifier = Modifier
                    .height(topBarHeightProvider())
                    .offset { heightOffset },
                searchTerm = searchTerm(),
                setSearchTerm = setSearchTerm,
                searchBarState = searchBarState,
                setSearchBarState = setSearchBarState
            ) {
                SearchFilters(
                    uiStateProvider = uiStateProvider,
                    onDateClick = { onFilterChipClick(BottomSheetContentType.DATE) },
                    onLocationClick = { onFilterChipClick(BottomSheetContentType.LOCATION) })
            }
        }
    ) { contentPadding ->
        ListScreenContent(
            uiStateProvider = uiStateProvider,
            contentPadding = contentPadding,
            onSelectDay = onSelectDay,
            onAddDay = onAddDay,
            onChangeYear = onChangeYear,
            setSearchBarState = setSearchBarState,
            setScreenMode = setScreenMode
        )
    }

    BottomSheetLayout(sheetState = bottomSheetState, focusRequester = focusRequester) {
        SearchBottomSheetContent(
            uiStateProvider = uiStateProvider,
            contentType = bottomSheetContentType,
            setQuery = {
                setQuery(it)
                scope.launch {
                    bottomSheetState.hide()
                }
            },
            getLocations = getLocations
        )
    }
}

