package me.ionice.snapshot.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    title: String? = null,
    isOpen: Boolean,
    initialTime: LocalTime = LocalTime.MIDNIGHT,
    onSelection: (LocalTime) -> Unit,
    onClose: () -> Unit
) {
    // TODO: refactor the actual time picker bit to its own component under ui.common
    var selectedHour by rememberSaveable { mutableStateOf(initialTime.hour) }
    var selectedMinute by rememberSaveable { mutableStateOf((initialTime.minute / 5) * 5) }

    val hours = (0..23).toList()
    val minutes = (0..55 step 5).toList()

    if (isOpen) {
        AlertDialog(
            onDismissRequest = onClose,
            title = {
                title?.let { Text(text = it) }
            },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        NumberDropdown(
                            options = hours,
                            selected = selectedHour,
                            onSelection = { selectedHour = it })
                    }
                    Text(":")
                    Box(Modifier.weight(1f)) {
                        NumberDropdown(
                            options = minutes,
                            selected = selectedMinute,
                            onSelection = { selectedMinute = it })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onSelection(LocalTime.of(selectedHour, selectedMinute)) }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onClose) {
                    Text(text = "Cancel")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberDropdown(
    label: String? = null,
    options: List<Int>,
    selected: Int,
    onSelection: (Int) -> Unit
) {
    if (!options.contains(selected)) {
        throw IllegalArgumentException("options does not contain initialValue")
    }

    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selected.toString(),
            onValueChange = {},
            label = { label?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.toString()) },
                    onClick = {
                        onSelection(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
