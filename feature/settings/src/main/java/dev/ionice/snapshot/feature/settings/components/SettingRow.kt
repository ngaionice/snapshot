package dev.ionice.snapshot.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import dev.ionice.snapshot.core.ui.components.PlaceholderText
import dev.ionice.snapshot.core.ui.titleMediumLarge
import dev.ionice.snapshot.feature.settings.R

/**
 * The base entry for each setting. Can be configured to provide button functionality.
 */
@Composable
internal fun SettingRow(
    mainLabel: String,
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    testTag: String? = null
) {

    // if a onClick is provided, enable button functionality
    val baseModifier =
        onClick?.let { Modifier.clickable(enabled = enabled, onClick = onClick) } ?: Modifier

    Row(
        modifier = baseModifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .let { if (!testTag.isNullOrEmpty()) it.testTag(testTag) else it },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = mainLabel,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                val textColor =
                    if (onClick != null && !enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurface
                Text(
                    text = mainLabel,
                    style = titleMediumLarge(),
                    color = textColor
                )
                if (secondaryLabel != null) {
                    Text(
                        text = secondaryLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (onClick != null && !enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingRowPlaceholder(
    mainLabel: String? = null,
    hasShape: Boolean = false,
    hasSecondary: Boolean = false
) {
    val tt = stringResource(R.string.tt_settings_row_placeholder)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .semantics { testTag = tt },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasShape) {
            Card(
                modifier = Modifier
                    .padding(top = 3.dp, bottom = 3.dp, end = 24.dp)
                    .height(22.dp)
                    .width(22.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.fade(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {}
        }
        Column(modifier = Modifier.weight(1f)) {
            if (mainLabel != null) {
                Text(
                    text = mainLabel,
                    style = titleMediumLarge(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                PlaceholderText(textStyle = titleMediumLarge(), modifier = Modifier.fillMaxWidth())
            }
            if (hasSecondary) {
                PlaceholderText(
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingRowPreview() {
    Column {
        SettingRow(
            mainLabel = "Backup & Restore",
            secondaryLabel = "100%",
            icon = Icons.Outlined.CloudSync,
            onClick = { })
        SettingRow(mainLabel = "Backup & Restore", icon = Icons.Outlined.CloudSync)
        SettingRow(mainLabel = "Backup & Restore")
    }
}

@Preview
@Composable
private fun SettingRowPlaceholderPreview() {
    Column {
        SettingRow(
            mainLabel = "Backup & Restore",
            secondaryLabel = "100%",
            icon = Icons.Outlined.CloudSync,
            onClick = { })
        SettingRowPlaceholder(hasSecondary = true)
        SettingRowPlaceholder(hasShape = true)
    }
}