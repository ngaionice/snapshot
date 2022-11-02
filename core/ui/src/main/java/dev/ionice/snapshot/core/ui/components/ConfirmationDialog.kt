package dev.ionice.snapshot.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.ionice.snapshot.core.ui.R

@Composable
fun ConfirmationDialog(
    isOpen: Boolean,
    titleText: String?,
    contentText: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onCancel,
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text(stringResource(R.string.common_dialog_cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.common_dialog_confirm))
                }
            },
            title = { titleText?.let { Text(titleText) } },
            text = { Text(contentText ?: "") })
    }
}