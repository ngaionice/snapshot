package dev.ionice.snapshot.feature.entries.list

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.navigation.Navigator
import dev.ionice.snapshot.core.navigation.SettingsHomeDestination
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.components.TopAppBar
import dev.ionice.snapshot.feature.entries.EntriesViewModel
import dev.ionice.snapshot.feature.entries.R
import dev.ionice.snapshot.feature.entries.list.components.EntryInsertDialog
import dev.ionice.snapshot.feature.entries.list.components.WeekSection
import dev.ionice.snapshot.feature.entries.list.components.YearSectionHeader
import dev.ionice.snapshot.feature.entries.list.components.getYearSectionContent

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
internal fun EntriesListScreen(
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

    val cdAdd = stringResource(R.string.add_entry)

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { setShowDialog(true) },
                modifier = Modifier.testTag(cdAdd)
            ) {
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