package dev.ionice.snapshot.ui.days.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.ui.components.SearchBar
import dev.ionice.snapshot.core.ui.components.SearchBarState

private val itemHeight = 64.dp

@Composable
fun DaySearchBar(
    searchTerm: String,
    setSearchTerm: (String) -> Unit,
    searchBarState: SearchBarState,
    setSearchBarState: (SearchBarState) -> Unit,
    modifier: Modifier = Modifier,
    searchFilters: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        SearchBar(
            searchBarState = searchBarState,
            setSearchBarState = setSearchBarState,
            placeholderText = "Search entries",
            searchTerm = searchTerm,
            setSearchTerm = setSearchTerm,
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier.padding(12.dp)
                )
            },
        )
        if (searchBarState != SearchBarState.NOT_SEARCHING) {
            searchFilters()
        }
    }
}