package me.ionice.snapshot.ui.entries.list

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.components.TopAppBar
import me.ionice.snapshot.ui.entries.EntriesViewModel
import me.ionice.snapshot.ui.entries.list.components.EntryInsertDialog
import me.ionice.snapshot.ui.entries.list.components.WeekSection
import me.ionice.snapshot.ui.entries.list.components.YearSectionHeader
import me.ionice.snapshot.ui.entries.list.components.getYearSectionContent
import me.ionice.snapshot.ui.navigation.Navigator
import me.ionice.snapshot.ui.settings.SettingsHomeDestination

@Composable
fun EntriesListRoute(viewModel: EntriesViewModel = hiltViewModel(), navigator: Navigator) {
    val uiState by viewModel.listUiState.collectAsState()

    EntriesListScreen(
        weekEntriesProvider = { uiState.weekUiState },
        yearEntriesProvider = { uiState.yearUiState },
        yearProvider = { uiState.year },
        onAddEntry = {
            viewModel.add(it)
            navigator.navigateToEntry(it)
        },
        onSelectEntry = navigator::navigateToEntry,
        onSelectSettings = { navigator.navigateToDestination(SettingsHomeDestination) },
        onChangeYear = viewModel::changeListYear
    )
}

@VisibleForTesting
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EntriesListScreen(
    weekEntriesProvider: () -> DaysUiState,
    yearEntriesProvider: () -> DaysUiState,
    yearProvider: () -> Int,
    onAddEntry: (Long) -> Unit,
    onSelectEntry: (Long) -> Unit,
    onSelectSettings: () -> Unit,
    onChangeYear: (Int) -> Unit
) {
    val (expandedWeek, setExpandedWeek) = remember { mutableStateOf(-1) }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val cdAdd = stringResource(R.string.entries_add_entry)

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { setShowDialog(true) }, modifier = Modifier.semantics { contentDescription = cdAdd }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = cdAdd)
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            LazyColumn {
                item {
                    WeekSection(
                        uiStateProvider = weekEntriesProvider,
                        onAddEntry = onAddEntry,
                        onSelectEntry = onSelectEntry
                    )
                }

                stickyHeader {
                    YearSectionHeader(
                        yearProvider = yearProvider,
                        onChangeYear = {
                            onChangeYear(it)
                            setExpandedWeek(-1)
                        })
                }

                getYearSectionContent(
                    uiStateProvider = yearEntriesProvider,
                    expandedWeek = expandedWeek,
                    setExpandedWeek = setExpandedWeek,
                    onSelectEntry = onSelectEntry
                )
            }
        }
        if (showDialog) {
            EntryInsertDialog(onDismiss = { setShowDialog(false) }, onAddEntry = onAddEntry)
        }
    }
}