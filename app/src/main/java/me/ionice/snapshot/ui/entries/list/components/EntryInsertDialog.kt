package me.ionice.snapshot.ui.entries.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.DatePicker
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@Composable
fun EntryInsertDialog(onDismiss: () -> Unit, onAddEntry: (Long) -> Unit) {
    val (date, setDate) = remember { mutableStateOf(LocalDate.now()) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf("") }

    val cdConfirm = stringResource(R.string.cd_entries_dialog_confirm)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select date", style = MaterialTheme.typography.labelSmall)
        },
        confirmButton = {
            Button(
                onClick = { onAddEntry(date.toEpochDay()) },
                enabled = errorMessage.isEmpty(),
                modifier = Modifier.semantics { contentDescription = cdConfirm }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = date.format(Utils.dateFormatter),
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    DatePicker(
                        date = date,
                        onSelectDate = setDate,
                        showErrorIcon = errorMessage.isNotEmpty(),
                        setErrorMessage = setErrorMessage,
                        allowFuture = false
                    )
                    Box(
                        modifier = Modifier.padding(
                            start = 16.dp, end = 16.dp, top = 4.dp, bottom = 0.dp
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.labelMedium,
                            color = LocalContentColor.current
                        )
                    }
                }
            }
        })
}