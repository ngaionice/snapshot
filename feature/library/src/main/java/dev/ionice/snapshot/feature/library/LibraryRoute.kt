package dev.ionice.snapshot.feature.library

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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.navigation.*
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.core.ui.components.PageSection
import dev.ionice.snapshot.core.ui.components.TopAppBar
import dev.ionice.snapshot.core.ui.screens.FunctionalityNotAvailableScreen
import dev.ionice.snapshot.feature.library.components.QuickAccess
import dev.ionice.snapshot.feature.library.components.Tags

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
        onSelectTag = { navigator.navigateToTag(it) },
        onSelectAllTags = { navigator.navigateToDestination(TagsListDestination) },
        onSelectFavorites = { navigator.navigateToDestination(FavoritesDestination) },
        onSelectRandom = { /* TODO */ },
        onSelectSearch = { navigator.navigateToDestination(SearchDestination) },
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
    onSelectRandom: () -> Unit,
    onSelectSearch: () -> Unit,
    onSelectSettings: () -> Unit
) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = onSelectSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.cd_topbar_settings)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSelectSearch) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
//                .verticalScroll(scrollState)
        ) {
            QuickAccess(onSelectFavorites = onSelectFavorites, onSelectRandom = onSelectRandom)
            Tags(
                tagsProvider = tagsProvider,
                onSelectTag = onSelectTag,
                onSelectViewAll = onSelectAllTags
            )
            PageSection(title = "Other features") {
                FunctionalityNotAvailableScreen("Coming soon!")
            }
        }
    }
}
