package dev.ionice.snapshot.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.ui.R
import java.time.LocalDate

@Composable
fun DateRangePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (start: LocalDate, end: LocalDate) -> Unit
) {
    val (proposedRange, setProposedDateRange) = remember {
        mutableStateOf<Pair<LocalDate?, LocalDate?>>(
            Pair(null, null)
        )
    }
    val actualRange =
        if (proposedRange.first != null && proposedRange.second != null &&
            proposedRange.first!!.compareTo(proposedRange.second) < 1
        ) {
            Pair(proposedRange.first!!, proposedRange.second!!)
        } else null

    val (leftErrMsg, setLeftErrMsg) = remember { mutableStateOf("") }
    val (rightErrMsg, setRightErrMsg) = remember { mutableStateOf("") }
    val (overrideRightErrIcon, setOverrideRightErrIcon) = remember { mutableStateOf<Boolean?>(null) }

    AlertDialog(
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(id = R.string.common_dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(
                enabled = actualRange != null,
                onClick = { onConfirm(actualRange!!.first, actualRange.second) }) {
                Text(stringResource(id = R.string.common_dialog_ok))
            }
        },
        title = {
            Text(stringResource(R.string.date_range_picker_dialog_title), style = MaterialTheme.typography.labelSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = actualRange?.let {
                        "${it.first.format(Utils.dateFormatter)} — ${
                            it.second.format(Utils.dateFormatter)
                        }"
                    } ?: stringResource(R.string.date_range_picker_dialog_date_placeholder),
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    DatePickerColumn(
                        modifier = Modifier
                            .weight(1f),
                        label = stringResource(R.string.date_range_picker_dialog_start_label),
                        date = proposedRange.first,
                        setDate = { setProposedDateRange(proposedRange.copy(first = it)) },
                        errorMessage = leftErrMsg,
                        setErrorMessage = setLeftErrMsg,
                        contentDescription = stringResource(R.string.cd_date_range_start_text_field)
                    )
                    DatePickerColumn(
                        modifier = Modifier
                            .weight(1f),
                        label = stringResource(R.string.date_range_picker_dialog_end_label),
                        date = proposedRange.second,
                        setDate = { setProposedDateRange(proposedRange.copy(second = it)) },
                        errorMessage = rightErrMsg,
                        setErrorMessage = setRightErrMsg,
                        showErrorIcon = overrideRightErrIcon,
                        contentDescription = stringResource(R.string.cd_date_range_end_text_field)
                    )
                }
            }
        }
    )

    LaunchedEffect(key1 = proposedRange) {
        val (first, second) = proposedRange
        if (first != null && second != null) {
            if (first > second) {
                setLeftErrMsg("Out of range")
                setOverrideRightErrIcon(true)
            } else {
                setLeftErrMsg("")
                setOverrideRightErrIcon(null)
            }
        }
    }
}

@Composable
private fun DatePickerColumn(
    modifier: Modifier,
    label: String,
    date: LocalDate?,
    setDate: (LocalDate) -> Unit,
    errorMessage: String,
    setErrorMessage: (String) -> Unit,
    showErrorIcon: Boolean? = null,
    contentDescription: String? = null
) {
    Column(modifier = modifier) {
        DatePicker(
            date = date,
            label = label,
            onSelectDate = setDate,
            showErrorIcon = showErrorIcon ?: errorMessage.isNotEmpty(),
            setErrorMessage = setErrorMessage,
            allowFuture = false,
            contentDescription = contentDescription
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