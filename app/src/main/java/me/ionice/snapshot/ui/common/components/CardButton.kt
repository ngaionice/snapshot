package me.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.ElevationTokens

/**
 * A button that supports an optional leading icon.
 *
 * Parent should be constrained horizontally as this composable fills the maximum width available.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    contentDescription: String = ""
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = ElevationTokens.Level2,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .alignByBaseline(),
                    imageVector = it,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

@Preview
@Composable
fun CardButtonPreview() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardButton(
            label = "Favorites",
            onClick = {},
            modifier = Modifier.weight(0.5f),
            leadingIcon = Icons.Filled.Star, contentDescription = "Favorites"
        )

        CardButton(
            label = "Favorites",
            onClick = {},
            modifier = Modifier.weight(0.5f),
            leadingIcon = Icons.Filled.Star, contentDescription = "Favorites"
        )
    }
}