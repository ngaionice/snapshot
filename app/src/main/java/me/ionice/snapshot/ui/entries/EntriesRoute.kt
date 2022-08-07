package me.ionice.snapshot.ui.entries

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import me.ionice.snapshot.ui.common.components.TopAppBar
import me.ionice.snapshot.ui.entries.components.ThisWeek
import me.ionice.snapshot.ui.entries.components.YearListHeader
import me.ionice.snapshot.ui.entries.components.getYearList

@Composable
fun EntriesRoute(
    viewModel: EntriesViewModel,
    onSelectEntry: (Long) -> Unit,
    onSelectSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    EntriesScreen(
        weekEntriesProvider = { uiState.weekUiState },
        yearEntriesProvider = { uiState.yearUiState },
        year = uiState.year,
        onAddEntry = {
            viewModel.addEntry(it)
            onSelectEntry(it)
        },
        onSelectEntry = onSelectEntry,
        onSelectSettings = onSelectSettings,
        onChangeYear = viewModel::changeViewingYear
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun EntriesScreen(
    weekEntriesProvider: () -> WeekUiState,
    yearEntriesProvider: () -> YearUiState,
    year: Int,
    onAddEntry: (Long) -> Unit,
    onSelectEntry: (Long) -> Unit,
    onSelectSettings: () -> Unit,
    onChangeYear: (Int) -> Unit
) {
    var expandedWeek by remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            IconButton(onClick = { /*TODO*/ }, enabled = false) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add entry")
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
                    year = year,
                    onChangeYear = {
                        onChangeYear(it)
                        expandedWeek = -1
                    }
                )
            }

            getYearList(
                uiStateProvider = yearEntriesProvider,
                expandedWeek = expandedWeek,
                setExpandedWeek = { expandedWeek = it },
                onSelectEntry = onSelectEntry
            )
        }
    }
}