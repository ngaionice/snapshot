package dev.ionice.snapshot.feature.tags.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

@Composable
internal fun PlaceholderList(modifier: Modifier = Modifier, contentDesc: String? = null) {
    // TODO: extract string
    LazyColumn(
        modifier = modifier.semantics { contentDescription = contentDesc ?: "Placeholder list" }
    ) {
        items(7) {
            PlaceholderListItem(
                headlineTextModifier = Modifier.fillParentMaxWidth(0.5f),
                supportingTextModifier = Modifier.fillParentMaxWidth(0.3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderListItem(
    modifier: Modifier = Modifier,
    headlineTextModifier: Modifier,
    supportingTextModifier: Modifier
) {
    ListItem(headlineText = {
        Text(
            "",
            modifier = headlineTextModifier
                .placeholder(
                    visible = true, highlight = PlaceholderHighlight.fade(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
        )
    }, supportingText = {
        Text(
            "",
            modifier = supportingTextModifier
                .placeholder(
                    visible = true, highlight = PlaceholderHighlight.fade(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
        )
    },
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    )
}