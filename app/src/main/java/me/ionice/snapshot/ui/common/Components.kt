package me.ionice.snapshot.ui.common

import android.widget.CalendarView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeaderBar(
    placeholderText: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearchStringChange: (String) -> Unit,
    onSearchBarActiveStateChange: ((Boolean) -> Unit)? = null
) {

    var searching by remember { mutableStateOf(false) }
    val searchString = rememberSaveable { mutableStateOf("") }

    // TODO: update to use updateTransition for even more control
    val horizontalPadding: Int by animateIntAsState(if (searching) 0 else 16)
    val verticalPadding: Int by animateIntAsState(if (searching) 0 else 8)

    val textFieldFocusRequester = remember { FocusRequester() }

    val onActiveStateChange: (Boolean) -> Unit = onSearchBarActiveStateChange ?: {}

    Card(
        onClick = {
            searching = true
            onActiveStateChange(true)
        },
        shape = if (searching) Shapes.None else Shapes.Full,
        modifier = Modifier.padding(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = (8 - verticalPadding).dp)
        ) {
            if (searching) {
                IconButton(onClick = {
                    searching = false
                    onActiveStateChange(false)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            } else {
                if (leadingIcon != null) {
                    leadingIcon()
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (searchString.value.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (searching) {
                    BasicTextField(
                        value = searchString.value,
                        onValueChange = {
                            searchString.value = it
                        }, maxLines = 1,
                        modifier = Modifier.focusRequester(textFieldFocusRequester),
                        textStyle = TextStyle.Default.copy(color = LocalContentColor.current),
                        cursorBrush = SolidColor(LocalContentColor.current)
                    )
                    textFieldFocusRequester.requestFocus()
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            if (searching) {
                IconButton(onClick = {
                    searchString.value = ""
                    textFieldFocusRequester.requestFocus()
                }) {
                    Icon(Icons.Filled.Cancel, contentDescription = "Clear")
                }
            } else {
                if (trailingIcon != null) {
                    trailingIcon()
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }

    LaunchedEffect(key1 = searchString.value) {
        onSearchStringChange(searchString.value)
    }

    LaunchedEffect(key1 = searching) {
        onActiveStateChange(searching)
    }

    BackHandler(enabled = searching) {
        searching = false
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
        }, onSearchStringChange = {}) {

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