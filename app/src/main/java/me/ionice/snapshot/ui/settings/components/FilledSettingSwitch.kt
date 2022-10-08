package me.ionice.snapshot.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.PlaceholderText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilledSettingSwitch(
    mainLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mainLabel,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun FilledSettingSwitchPlaceholder() {
    Card(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaceholderText(
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = false, enabled = false, onCheckedChange = {})
        }
    }
}

@Preview
@Composable
private fun FilledSwitchPreview() {
    FilledSettingSwitch(mainLabel = "Use location", checked = false, onCheckedChange = { })
}

@Preview
@Composable
private fun FilledSwitchPlaceholderPreview() {
    FilledSettingSwitchPlaceholder()
}