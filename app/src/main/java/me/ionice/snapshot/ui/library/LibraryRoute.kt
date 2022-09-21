package me.ionice.snapshot.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.ui.common.components.TopAppBar
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.navigation.Navigator
import me.ionice.snapshot.ui.settings.SettingsHomeDestination

@Composable
fun LibraryRoute(
    viewModel: LibraryViewModel = hiltViewModel(),
    navigator: Navigator
) {
    val uiState by viewModel.uiState.collectAsState()

    LibraryScreen(
        memoriesProvider = { uiState.memoriesUiState },
        locationProvider = { uiState.locationsUiState },
        tagsProvider = { uiState.tagsUiState },
        onSelectEntry = navigator::navigateToEntry,
        onSelectTag = { /* TODO */ },
        onSelectAllTags = { /* TODO */ },
        onSelectFavorites = { /* TODO */ },
        onSelectSearch = { /* TODO */ },
        onSelectSettings = { navigator.navigateToDestination(SettingsHomeDestination) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    memoriesProvider: () -> DaysUiState,
    locationProvider: () -> LocationsUiState,
    tagsProvider: () -> TagsUiState,
    onSelectEntry: (Long) -> Unit,
    onSelectTag: (Long) -> Unit,
    onSelectAllTags: () -> Unit,
    onSelectFavorites: () -> Unit,
    onSelectSearch: () -> Unit,
    onSelectSettings: () -> Unit
) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSelectSearch) {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
//                .verticalScroll(scrollState)
        ) {
            FunctionalityNotAvailableScreen("Coming soon!")
        }
    }
}