package dev.ionice.snapshot.feature.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.ui.components.PageSection
import dev.ionice.snapshot.feature.search.R

@Composable
internal fun SearchHistory(recentSearches: List<String>, onSearch: (String) -> Unit) {
    if (recentSearches.isEmpty()) return

    PageSection(
        title = stringResource(R.string.recent_searches_header),
        headerTextColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column {
            recentSearches.forEach {
                val cd = stringResource(R.string.cd_recent_search, it)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSearch(it) }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .semantics { contentDescription = cd },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null
                    )
                    Text(text = it)
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchHistoryPreview() {
    SearchHistory(recentSearches = listOf("snapshot", "work", "swimming"), onSearch = {})
}