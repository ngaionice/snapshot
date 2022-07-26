package me.ionice.snapshot.ui.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R

@Composable
fun FunctionalityNotYetAvailableDialog(isOpen: Boolean, onClose: () -> Unit) {
    if (isOpen) {
        AlertDialog(onDismissRequest = onClose, confirmButton = {
            TextButton(onClick = onClose) {
                Text(
                    text = stringResource(
                        R.string.common_functionality_na_dialog_close
                    )
                )
            }
        }, text = { Text(stringResource(R.string.common_functionality_soon)) })
    }
}