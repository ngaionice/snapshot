package dev.ionice.snapshot.ui.favorites

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.R
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.database.model.DayEntity
import dev.ionice.snapshot.core.database.model. DayProperties
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.components.PlaceholderText
import dev.ionice.snapshot.core.ui.screens.BaseScreen
import dev.ionice.snapshot.ui.navigation.Navigator
import java.time.LocalDate

@Composable
fun FavoritesRoute(viewModel: FavoritesViewModel = hiltViewModel(), navigator: Navigator) {
    val uiState by viewModel.uiState.collectAsState()

    FavoritesScreen(
        uiStateProvider = { uiState },
        onSelectEntry = navigator::navigateToEntry,
        onBack = navigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
@Composable
fun FavoritesScreen(
    uiStateProvider: () -> FavoritesUiState,
    onSelectEntry: (Long) -> Unit,
    onBack: () -> Unit
) {

    BaseScreen(headerText = "Favorites", navigationIcon = { BackButton(onBack = onBack) }) {
        when (val uiState = uiStateProvider()) {
            is FavoritesUiState.Loading -> FavoritesLoading()
            is FavoritesUiState.Error -> FavoritesError()
            is FavoritesUiState.Success -> FavoritesList(
                entriesProvider = { uiState.entries },
                locationsProvider = { uiState.locations },
                onSelectEntry = onSelectEntry
            )
        }
    }
}

@Composable
private fun FavoritesList(
    entriesProvider: () -> List<DayEntity>,
    locationsProvider: () -> List<LocationPropertiesEntity>,
    onSelectEntry: (Long) -> Unit
) {
    val entries = entriesProvider()
    val locations = locationsProvider().associateBy({ it.id }, { it.name })
    LazyColumn(modifier = Modifier.testTag(stringResource(R.string.tt_favorites_list))) {
        if (entries.isEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillParentMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.tt_favorites_none))
                }
            }
        } else {
            entries.forEach { day ->
                item {
                    FavoritesEntry(
                        entry = day.properties,
                        location = day.location?.let { locations[it.locationId] },
                        onSelect = { onSelectEntry(day.properties.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesEntry(entry: DayProperties, location: String?, onSelect: () -> Unit) {
    Column(modifier = Modifier
        .clickable { onSelect() }
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .testTag(stringResource(R.string.tt_favorites_entry))
    ) {
        Text(
            text = Utils.dateFormatter.format(LocalDate.ofEpochDay(entry.id)),
            style = MaterialTheme.typography.titleMedium
        )
        location?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
private fun FavoritesLoading() {
    Column(modifier = Modifier.testTag(stringResource(R.string.tt_favorites_loading))) {
        (1..10).forEach { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                PlaceholderText(
                    textStyle = MaterialTheme.typography.titleMedium,
                    Modifier.width(150.dp)
                )
                PlaceholderText(
                    textStyle = MaterialTheme.typography.labelSmall,
                    Modifier.width(50.dp)
                )
            }
        }
    }
}

@Composable
private fun FavoritesError() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.favorites_load_error_message))
    }
}