package dev.ionice.snapshot.feature.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.core.ui.components.PageSection
import dev.ionice.snapshot.core.ui.components.SquareImageCard
import dev.ionice.snapshot.core.ui.components.SquareImageCardPlaceholder
import dev.ionice.snapshot.feature.library.R

private const val ITEM_WIDTH = 0.3f

@Composable
internal fun Tags(
    tagsProvider: () -> TagsUiState,
    onSelectTag: (Long) -> Unit,
    onSelectViewAll: () -> Unit
) {
    PageSection(
        title = stringResource(R.string.tags_header),
        headerAction = {
            TextButton(onClick = onSelectViewAll) {
                Text(text = stringResource(R.string.tags_view_all))
            }
        }
    ) {
        when (val state = tagsProvider()) {
            is TagsUiState.Loading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(4) {
                        SquareImageCardPlaceholder(modifier = Modifier.fillParentMaxWidth(ITEM_WIDTH))
                    }
                }
            }
            is TagsUiState.Error -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.tags_load_failure))
                }
            }
            is TagsUiState.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.data.forEach {
                        item {
                            TagCard(
                                tagProvider = { it },
                                onSelectTag = { onSelectTag(it.id) },
                                modifier = Modifier.fillParentMaxWidth(ITEM_WIDTH)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagCard(
    tagProvider: () -> Tag,
    onSelectTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = tagProvider()

    SquareImageCard(displayText = tag.name, modifier = modifier, onClick = onSelectTag)
}