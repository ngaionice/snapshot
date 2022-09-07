//package me.ionice.snapshot.ui.days.components
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.runtime.Composable
//import me.ionice.snapshot.ui.common.components.SearchBarState
//import me.ionice.snapshot.ui.common.screens.LoadingScreen
//import me.ionice.snapshot.ui.days.DayListUiState
//import me.ionice.snapshot.ui.days.DayListViewModelState
//import me.ionice.snapshot.ui.days.screens.OverviewScreen
//import me.ionice.snapshot.ui.days.screens.SearchOptionsScreen
//import me.ionice.snapshot.ui.days.screens.SearchResultsScreen
//
//@Composable
//fun ListScreenContent(
//    uiStateProvider: () -> DayListUiState,
//    contentPadding: PaddingValues,
//    onSelectDay: (Long) -> Unit,
//    onAddDay: (Long) -> Unit,
//    onChangeYear: (Int) -> Unit,
//    setSearchBarState: (SearchBarState) -> Unit,
//    setScreenMode: (DayListViewModelState.ScreenMode) -> Unit
//) {
//    when (val uiState = uiStateProvider()) {
//        is DayListUiState.Loading -> {
//            LoadingScreen()
//        }
//        is DayListUiState.Overview -> OverviewScreen(
//            contentPadding = contentPadding,
//            daysInWeek = uiState.weekEntries,
//            daysInYear = uiState.yearEntries,
//            year = uiState.year,
//            onSelectDay = onSelectDay,
//            onAddDay = onAddDay,
//            onChangeYear = onChangeYear
//        )
//        is DayListUiState.Search.Options -> SearchOptionsScreen(
//            contentPadding = contentPadding,
//            uiState = uiState,
//            onSearch = {
//                setSearchBarState(SearchBarState.INACTIVE)
//                setScreenMode(DayListViewModelState.ScreenMode.SEARCH_RESULTS)
//            },
//            onSelectDayFromQuickResults = {
//                onSelectDay(it)
//                setSearchBarState(SearchBarState.NOT_SEARCHING)
//            }
//        )
//        is DayListUiState.Search.Results -> SearchResultsScreen(
//            contentPadding = contentPadding,
//            searchTerm = uiState.query.searchTerm,
//            results = uiState.results,
//            onSelectDay = {
//                onSelectDay(it)
//                setSearchBarState(SearchBarState.INACTIVE)
//            }
//        )
//    }
//}