package me.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.LocationProperties
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.components.PageSectionContent
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen

@Composable
fun EntryLocationSection(
    uiStateProvider: () -> LocationsUiState,
    editing: Boolean,
    selectedLocation: LocationEntry?,
    onSelectLocation: (LocationProperties) -> Unit,
    onAddLocation: (String, Coordinates) -> Unit
) {
    when (val uiState = uiStateProvider()) {
        is LocationsUiState.Loading -> {
            LoadingScreen()
        }
        is LocationsUiState.Error -> {
            FunctionalityNotAvailableScreen("Failed to load location data.")
        }
        is LocationsUiState.Success -> {
            PageSectionContent {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (editing) {
                            LocationDropdownSelector(
                                locationsProvider = { uiState.data },
                                selectedLocation = selectedLocation,
                                onSelectLocation = onSelectLocation
                            )
                            AutoDetectLocationButton(onAddLocation = onAddLocation)
                        } else {
                            LocationIndicator(
                                locationsProvider = { uiState.data },
                                selectedLocation = selectedLocation
                            )
                        }
                    }
                    FunctionalityNotAvailableScreen("Map display available soon!")
                }
            }
        }
    }
}

@Composable
private fun LocationIndicator(
    locationsProvider: () -> List<LocationProperties>,
    selectedLocation: LocationEntry?
) {
    val locations = locationsProvider()
    val tt = stringResource(R.string.tt_entries_single_location_info_display)
    Card(modifier = Modifier
        .fillMaxWidth()
        .semantics { testTag = tt }) {
        Column(modifier = Modifier.padding(16.dp)) {
            selectedLocation?.let { loc ->
                locations.find { it.id == loc.locationId }?.let { properties ->
                    Text(text = properties.name)
                    properties.let {
                        Text(
                            text = "${it.coordinates.lat}, ${it.coordinates.lon}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } ?: Text(stringResource(R.string.entries_single_location_placeholder))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationDropdownSelector(
    locationsProvider: () -> List<LocationProperties>,
    selectedLocation: LocationEntry?,
    onSelectLocation: (LocationProperties) -> Unit
) {
    val locations = locationsProvider()
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val tt = stringResource(R.string.tt_entries_single_location_selector)

    val selectedLocationProperties =
        selectedLocation?.let { loc -> locations.find { it.id == loc.locationId } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
        modifier = Modifier.semantics { testTag = tt }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selectedLocationProperties?.name ?: "Select a location",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
            locations.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelectLocation(option)
                        setExpanded(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun AutoDetectLocationButton(onAddLocation: (String, Coordinates) -> Unit) {
    IconButton(onClick = { /*TODO*/ }, enabled = false) {
        Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "auto-detect location")
    }
}