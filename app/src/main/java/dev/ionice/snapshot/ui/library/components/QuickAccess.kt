package dev.ionice.snapshot.ui.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.ui.components.CardButton
import dev.ionice.snapshot.core.ui.components.PageSectionContent

@Composable
fun QuickAccess(onSelectFavorites: () -> Unit, onSelectRandom: () -> Unit) {
    PageSectionContent {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardButton(
                label = "Favorites",
                onClick = onSelectFavorites,
                modifier = Modifier.weight(1f).testTag("LibraryQuickAccessFavorites"),
                leadingIcon = Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorites",
            )

            CardButton(
                label = "Random",
                onClick = onSelectRandom,
                modifier = Modifier.weight(1f).testTag("LibraryQuickAccessRandom"),
                enabled = false,
                leadingIcon = Icons.Outlined.Casino,
                contentDescription = "Random",
            )
        }
    }
}