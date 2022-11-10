package dev.ionice.snapshot.feature.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.feature.search.R

@Composable
internal fun SearchButton(searchString: String, onSearch: () -> Unit) {
    if (searchString.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSearch)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag(stringResource(R.string.tt_search_button)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ManageSearch,
            contentDescription = stringResource(R.string.cd_search_action)
        )
        Text(text = stringResource(R.string.search_button_msg, searchString))
    }
}