package dev.ionice.snapshot.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.R
import dev.ionice.snapshot.core.ui.components.TimePicker
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    initialTime: LocalTime = LocalTime.MIDNIGHT,
    onSelection: (LocalTime) -> Unit,
    onClose: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(text = "Select time", style = MaterialTheme.typography.labelSmall)
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimePicker(initialTime = selectedTime, onSelectTime = { selectedTime = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSelection(selectedTime) },
                modifier = Modifier.testTag(stringResource(R.string.tt_settings_time_picker_confirm))
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text(text = "Cancel")
            }
        }
    )
}