package dev.ionice.snapshot.feature.search.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.feature.search.R

@Composable
internal fun SearchBar(
    searchString: String,
    setSearchString: (String) -> Unit,
    isActive: Boolean,
    setIsActive: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val horizontalPadding: Int by animateIntAsState(
        if (isActive) 0 else 24,
        tween(durationMillis = 100)
    )
    val verticalPadding: Int by animateIntAsState(
        if (isActive) 0 else 8,
        tween(durationMillis = 100)
    )

    val focusRequester = remember { FocusRequester() }

    SearchBarBase(
        isActive = isActive,
        setIsActive = setIsActive,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        modifier = modifier
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = (8 - verticalPadding).dp)
            ) {
                LeadingIcon(onClick = onBack)

                Spacer(modifier = Modifier.width(4.dp))

                Box(modifier = Modifier.weight(1f)) {
                    SearchBarTextField(
                        modifier = Modifier.offset(y = (-1).dp),
                        isActive = isActive,
                        searchTerm = searchString,
                        onSearchTermChange = setSearchString,
                        focusRequester = focusRequester
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                TrailingIcon(onClick = {
                    setSearchString("")
                    setIsActive(true)
                })
            }
            if (isActive) {
                HorizontalDivider()
            }
        }
    }

    LaunchedEffect(key1 = searchString) {
        if (isActive) {
            setSearchString(searchString)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarBase(
    isActive: Boolean,
    setIsActive: (Boolean) -> Unit,
    horizontalPadding: Int,
    verticalPadding: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        onClick = { setIsActive(true) },
        shape = if (isActive) RoundedCornerShape(0) else RoundedCornerShape(100),
        modifier = modifier
            .padding(
                horizontal = horizontalPadding.dp,
                vertical = verticalPadding.dp
            )
            .testTag(stringResource(R.string.tt_search_bar_base)),
        colors = if (isActive)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        else CardDefaults.cardColors()
    ) {
        content()
    }
}

@Composable
private fun LeadingIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_search_bar_back)
        )
    }
}

@Composable
private fun TrailingIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Filled.Cancel,
            contentDescription = stringResource(R.string.cd_search_bar_clear)
        )
    }
}

@Composable
private fun SearchBarTextField(
    isActive: Boolean,
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    if (searchTerm.isEmpty() || !isActive) {
        Text(
            modifier = modifier,
            text = searchTerm.ifEmpty { stringResource(R.string.search_bar_placeholder) },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    if (isActive) {
        BasicTextField(
            value = searchTerm,
            onValueChange = onSearchTermChange,
            singleLine = true,
            modifier = modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .testTag(stringResource(R.string.tt_search_bar_text_field)),
            textStyle = TextStyle.Default.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontFamily = LocalTextStyle.current.fontFamily
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }

    LaunchedEffect(key1 = isActive) {
        if (isActive) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    var searchBarState by remember { mutableStateOf(false) }
    var searchTerm by remember { mutableStateOf("") }

    SearchBar(
        searchString = searchTerm,
        setSearchString = { searchTerm = it },
        onBack = {},
        isActive = searchBarState,
        setIsActive = { searchBarState = it }
    )
}