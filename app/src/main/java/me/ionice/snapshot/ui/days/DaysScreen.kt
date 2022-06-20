package me.ionice.snapshot.ui.days

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.days.screens.EntryNotAvailableScreen
import me.ionice.snapshot.ui.days.screens.EntryScreen
import me.ionice.snapshot.ui.days.screens.ListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaysScreen(viewModel: DaysViewModel, showBottomNav: (Boolean) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.loading) {
        BaseScreen(headerText = stringResource(R.string.day_screen_placeholder_header)) {
            LoadingScreen()
        }
    } else {
        when (uiState) {
            is DayUiState.DayList -> {
                ListScreen(
                    uiState = uiState as DayUiState.DayList,
                    onDaySelect = { viewModel.selectDay(it) },
                    onDayAdd = { viewModel.addDay(it) },
                    onSwitchYear = { viewModel.switchYear(it) },
                    onSearch = {
                        viewModel.search(
                            DaySearchQuery(
                                (uiState as DayUiState.DayList).year,
                                it
                            )
                        )
                    },
                    onClearSearch = { viewModel.clearSearch() }
                )
            }
            is DayUiState.DayEntryNotFound -> {
                EntryNotAvailableScreen(
                    uiState = uiState as DayUiState.DayEntryNotFound,
                    onDayAdd = {
                        viewModel.addDay(it)
                    },
                    onBack = { viewModel.deselectDay() })
            }
            is DayUiState.DayEntryFound -> {
                showBottomNav(false)
                EntryScreen(
                    uiState = uiState as DayUiState.DayEntryFound,
                    onLocationChange = { viewModel.setLocation(it) },
                    onSummaryChange = { viewModel.setSummary(it) },
                    onMetricAdd = { viewModel.addMetric(it) },
                    onMetricDelete = { viewModel.removeMetric(it) },
                    onMetricChange = { index, value -> viewModel.updateMetric(index, value) },
                    onEntrySave = { viewModel.saveDay() },
                    onEntryExit = {
                        viewModel.deselectDay()
                        showBottomNav(true)
                    })
            }
        }
    }
}