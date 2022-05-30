package me.ionice.snapshot.ui.common

import android.widget.CalendarView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.ionice.snapshot.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )
    }
}

@Composable
fun AddFAB(onClick: () -> Unit, description: String) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = description)
    }
}

/**
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchHeaderBar(
    placeholderText: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearchStringChange: (String) -> Unit
) {

    var searching by remember { mutableStateOf(false) }
    val searchString = rememberSaveable { mutableStateOf("") }

    AnimatedContent(targetState = searching) { targetState ->
        when (targetState) {
            false -> {
                Card(
                    onClick = { searching = true },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon()
                        } else {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = placeholderText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        if (trailingIcon != null) {
                            trailingIcon()
                        } else {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }
            true -> {
                TextField(
                    value = searchString.value,
                    onValueChange = {
                        searchString.value = it
                        onSearchStringChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    leadingIcon = {
                        IconButton(onClick = { searching = false }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    placeholder = {
                        Text(text = placeholderText, style = MaterialTheme.typography.bodyMedium)
                    }
                )
            }
        }

    }
}

@Preview
@Composable
fun SearchBarPreview() {

    SearchHeaderBar(placeholderText = "Search in day summaries",
        leadingIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        trailingIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Year")
            }
        }) {

    }
}

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

@Composable
fun FunctionalityNotYetAvailableScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.common_functionality_soon),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FunctionalityNotAvailableScreen(reason: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.common_functionality_na_with_reason, reason),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

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

@Composable
fun SectionHeader(icon: ImageVector? = null, displayText: String) {
    Row(
        Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = displayText, tint = MaterialTheme.colorScheme.primary)
        }
        Text(
            displayText,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Based on code from Joao Gavazzi.
 */
@Composable
fun DatePicker(onSelect: (Long) -> Unit, onDismissRequest: () -> Unit) {
    val selDate = remember { mutableStateOf(LocalDate.now()) }

    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.common_calendar_select_date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = selDate.value.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(16.dp))
            }

            CustomCalendarView(onDateSelected = {
                selDate.value = it
            })

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors()
                ) {
                    Text(
                        text = stringResource(R.string.common_dialog_cancel)
                    )
                }

                Button(
                    onClick = {
                        onSelect(selDate.value.toEpochDay())
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors()
                ) {
                    Text(
                        text = stringResource(R.string.common_dialog_ok)
                    )
                }

            }
        }
    }
}

@Composable
private fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit) {
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { CalendarView(it) },
        update = { view ->
            view.minDate = 0
            view.maxDate = System.currentTimeMillis()

            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate.of(year, month + 1, dayOfMonth)
                )
            }
        }
    )
}