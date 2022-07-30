package me.ionice.snapshot.ui.common.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeaderBar(
    placeholderText: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearchStringChange: (String) -> Unit,
    onSearchBarActiveStateChange: ((Boolean) -> Unit)? = null,
) {

    var searching by rememberSaveable { mutableStateOf(false) }
    var searchString by rememberSaveable { mutableStateOf("") }

    val horizontalPadding: Int by animateIntAsState(if (searching) 0 else 24)
    val verticalPadding: Int by animateIntAsState(if (searching) 0 else 8)

    val textFieldFocusRequester = remember { FocusRequester() }

    val onActiveStateChange: (Boolean) -> Unit = onSearchBarActiveStateChange ?: {}

    Card(
        onClick = {
            searching = true
            onActiveStateChange(true)
        },
        shape = if (searching) Shapes.None else Shapes.Full,
        modifier = modifier.padding(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = (8 - verticalPadding).dp)
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
                if (searchString.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (searching) {
                    BasicTextField(
                        value = searchString,
                        onValueChange = {
                            searchString = it
                        }, maxLines = 1,
                        modifier = Modifier.focusRequester(textFieldFocusRequester).fillMaxWidth(),
                        textStyle = TextStyle.Default.copy(color = LocalContentColor.current),
                        cursorBrush = SolidColor(LocalContentColor.current)
                    )
                    textFieldFocusRequester.requestFocus()
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            if (searching) {
                IconButton(onClick = {
                    searchString = ""
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

    if (searching) {
        LaunchedEffect(key1 = searchString) {
            onSearchStringChange(searchString)
        }
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