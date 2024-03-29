package dev.ionice.snapshot.feature.entries.list.components

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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.ui.components.DatePicker
import dev.ionice.snapshot.feature.entries.R
import java.time.LocalDate

@Composable
internal fun EntryInsertDialog(onDismiss: () -> Unit, onAddEntry: (Long) -> Unit) {
    val (date, setDate) = remember { mutableStateOf(LocalDate.now()) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf("") }

    val ttConfirm = stringResource(R.string.tt_dialog_confirm)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.dialog_title), style = MaterialTheme.typography.labelSmall)
        },
        confirmButton = {
            TextButton(
                onClick = { onAddEntry(date.toEpochDay()) },
                enabled = errorMessage.isEmpty(),
                modifier = Modifier.semantics { testTag = ttConfirm }) {
                Text(stringResource(dev.ionice.snapshot.core.ui.R.string.button_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(dev.ionice.snapshot.core.ui.R.string.button_cancel))
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