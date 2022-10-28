package dev.ionice.snapshot.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.ui.common.components.PlaceholderText
import dev.ionice.snapshot.ui.common.titleMediumLarge

@Composable
fun SettingSwitch(
    mainLabel: String,
    secondaryLabel: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = mainLabel, style = titleMediumLarge())
            if (secondaryLabel != null) {
                Text(
                    text = secondaryLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.let {
                if (!testTag.isNullOrEmpty()) it.testTag(testTag) else it
            })
    }
}

@Composable
fun SettingSwitchPlaceholder(mainLabel: String? = null, hasSecondary: Boolean = false) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
        ) {
            if (mainLabel != null) {
                Text(text = mainLabel, style = titleMediumLarge())
            } else {
                PlaceholderText(titleMediumLarge(), Modifier.fillMaxWidth())
            }
            if (hasSecondary) {
                PlaceholderText(MaterialTheme.typography.labelMedium, Modifier.width(100.dp))
            }
        }
        Switch(checked = false, enabled = false, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun SettingSwitchPreview() {
    Column {
        SettingSwitch(
            mainLabel = "Enable test",
            secondaryLabel = "Secondary text",
            checked = true,
            onCheckedChange = {}
        )
        SettingSwitch(
            mainLabel = "Enable test 2",
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
private fun SettingSwitchPlaceholderPreview() {
    Column {
        SettingSwitchPlaceholder(hasSecondary = true)
        SettingSwitchPlaceholder()
    }
}