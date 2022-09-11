package me.ionice.snapshot.ui.entries.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import me.ionice.snapshot.ui.common.components.TopAppBar
import me.ionice.snapshot.ui.entries.list.components.EntryInsertDialog
import me.ionice.snapshot.ui.entries.list.components.ThisWeek
import me.ionice.snapshot.ui.entries.list.components.YearListHeader
import me.ionice.snapshot.ui.entries.list.components.getYearList
import me.ionice.snapshot.ui.navigation.Navigator
import me.ionice.snapshot.ui.settings.SettingsHomeDestination

@Composable
fun EntriesListRoute(viewModel: EntriesListViewModel, navigator: Navigator) {
    val uiState by viewModel.uiState.collectAsState()

    EntriesListScreen(
        weekEntriesProvider = { uiState.weekUiState },
        yearEntriesProvider = { uiState.yearUiState },
        yearProvider = { uiState.year },
        onAddEntry = {
            viewModel.addEntry(it)
            navigator.navigateToEntry(it)
        },
        onSelectEntry = navigator::navigateToEntry,
        onSelectSettings = { navigator.navigateToDestination(SettingsHomeDestination) },
        onChangeYear = viewModel::changeViewingYear
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun EntriesListScreen(
    weekEntriesProvider: () -> WeekUiState,
    yearEntriesProvider: () -> YearUiState,
    yearProvider: () -> Int,
    onAddEntry: (Long) -> Unit,
    onSelectEntry: (Long) -> Unit,
    onSelectSettings: () -> Unit,
    onChangeYear: (Int) -> Unit
) {
    val (expandedWeek, setExpandedWeek) = remember { mutableStateOf(-1) }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { setShowDialog(true) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add entry")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                ThisWeek(
                    uiStateProvider = weekEntriesProvider,
                    onAddEntry = onAddEntry,
                    onSelectEntry = onSelectEntry
                )
            }

            stickyHeader {
                YearListHeader(
                    yearProvider = yearProvider,
                    onChangeYear = {
                        onChangeYear(it)
                        setExpandedWeek(-1)
                    })
            }

            getYearList(
                uiStateProvider = yearEntriesProvider,
                expandedWeek = expandedWeek,
                setExpandedWeek = setExpandedWeek,
                onSelectEntry = onSelectEntry
            )
        }
        if (showDialog) {
            EntryInsertDialog(onDismiss = { setShowDialog(false) }, onAddEntry = onAddEntry)
        }
    }
}