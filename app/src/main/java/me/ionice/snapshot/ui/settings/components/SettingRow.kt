package me.ionice.snapshot.ui.settings.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.PlaceholderText

/**
 * The base entry for each setting. Can be configured to provide button functionality.
 */
@Composable
fun SettingRow(
    mainLabel: String,
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    // if a onClick is provided, enable button functionality
    val baseModifier =
        onClick?.let { Modifier.clickable(enabled = !disabled, onClick = onClick) } ?: Modifier

    Row(
        modifier = baseModifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
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
                    if (onClick == null || disabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                Text(
                    text = mainLabel,
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
                if (secondaryLabel != null) {
                    Text(
                        text = secondaryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingRowPlaceholder(hasShape: Boolean = false, hasSecondary: Boolean = false) {
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
            PlaceholderText(textStyle = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
            if (hasSecondary) {
                PlaceholderText(
                    textStyle = MaterialTheme.typography.labelSmall,
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