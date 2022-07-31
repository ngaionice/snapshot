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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class SearchBarState {
    ACTIVE,
    INACTIVE,
    NOT_SEARCHING
}

@Composable
fun SearchHeaderBar(
    searchString: String,
    setSearchString: (String) -> Unit,
    searchBarState: SearchBarState,
    setSearchBarState: (SearchBarState) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {

    val horizontalPadding: Int by animateIntAsState(if (searchBarState == SearchBarState.ACTIVE) 0 else 24)
    val verticalPadding: Int by animateIntAsState(if (searchBarState == SearchBarState.ACTIVE) 0 else 8)

    val textFieldFocusRequester = remember { FocusRequester() }

    SearchBarBase(
        searchBarState = searchBarState,
        setSearchBarState = setSearchBarState,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = (8 - verticalPadding).dp)
        ) {
            SearchBarLeadingIcon(
                searchBarState = searchBarState,
                setSearchBarState = {
                    setSearchBarState(it)
                    if (it == SearchBarState.NOT_SEARCHING) {
                        setSearchString("")
                    }
                },
                leadingIcon = leadingIcon
            )

            Spacer(modifier = Modifier.width(4.dp))

            Box(modifier = Modifier.weight(1f)) {
                SearchBarTextField(
                    searchBarState = searchBarState,
                    searchString = searchString,
                    onSearchStringChange = setSearchString,
                    focusRequester = textFieldFocusRequester,
                    placeholderText = placeholderText
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            SearchBarTrailingIcon(
                searchBarState = searchBarState,
                onSearchStringClear = {
                    setSearchString("")
                    textFieldFocusRequester.requestFocus()
                },
                trailingIcon = trailingIcon
            )
        }
    }

    LaunchedEffect(key1 = searchString) {
        if (searchBarState == SearchBarState.ACTIVE) {
            setSearchString(searchString)
        }
    }

    BackHandler(enabled = searchBarState != SearchBarState.NOT_SEARCHING) {
        setSearchBarState(SearchBarState.NOT_SEARCHING)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarBase(
    searchBarState: SearchBarState,
    setSearchBarState: (SearchBarState) -> Unit,
    horizontalPadding: Int,
    verticalPadding: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        onClick = {
            setSearchBarState(SearchBarState.ACTIVE)
        },
        shape = if (searchBarState == SearchBarState.ACTIVE) Shapes.None else Shapes.Full,
        modifier = modifier.padding(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp
        )
    ) {
        content()
    }
}

@Composable
private fun SearchBarLeadingIcon(
    searchBarState: SearchBarState,
    setSearchBarState: (SearchBarState) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    if (searchBarState == SearchBarState.NOT_SEARCHING) {
        if (leadingIcon != null) {
            leadingIcon()
        } else {
            Spacer(modifier = Modifier.width(12.dp))
        }
    } else {
        IconButton(onClick = {
            setSearchBarState(SearchBarState.NOT_SEARCHING)
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

@Composable
private fun SearchBarTrailingIcon(
    searchBarState: SearchBarState,
    onSearchStringClear: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    if (searchBarState == SearchBarState.NOT_SEARCHING) {
        if (trailingIcon != null) {
            trailingIcon()
        } else {
            Spacer(modifier = Modifier.width(12.dp))
        }
    } else {
        IconButton(onClick = onSearchStringClear) {
            Icon(Icons.Filled.Cancel, contentDescription = "Clear")
        }
    }
}

@Composable
private fun SearchBarTextField(
    searchBarState: SearchBarState,
    searchString: String,
    onSearchStringChange: (String) -> Unit,
    focusRequester: FocusRequester,
    placeholderText: String
) {
    if (searchString.isEmpty()) {
        Text(
            text = placeholderText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    if (searchBarState == SearchBarState.ACTIVE) {
        BasicTextField(
            value = searchString,
            onValueChange = onSearchStringChange,
            maxLines = 1,
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            textStyle = TextStyle.Default.copy(color = LocalContentColor.current),
            cursorBrush = SolidColor(LocalContentColor.current)
        )
        focusRequester.requestFocus()
    } else {
        Text(
            text = searchString,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Preview
@Composable
fun SearchBarPreview() {
    var searchBarState by remember { mutableStateOf(SearchBarState.NOT_SEARCHING) }
    var searchString by remember { mutableStateOf("") }

    SearchHeaderBar(
        placeholderText = "Search summaries",
        leadingIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        trailingIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Year")
            }
        },
        searchString = searchString,
        setSearchString = { searchString = it },
        searchBarState = searchBarState,
        setSearchBarState = { searchBarState = it }
    )
}