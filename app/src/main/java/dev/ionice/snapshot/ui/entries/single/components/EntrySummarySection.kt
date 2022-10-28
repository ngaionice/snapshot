package dev.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.R
import dev.ionice.snapshot.ui.common.components.PageSectionContent

@Composable
fun EntrySummarySection(editing: Boolean, text: String, onTextChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val (textFieldValue, setTextFieldValue) = remember { mutableStateOf(TextFieldValue(text)) }

    val tt = stringResource(R.string.tt_entries_single_summary)

    LaunchedEffect(key1 = text) {
        setTextFieldValue(textFieldValue.copy(text = text))
    }

    LaunchedEffect(key1 = editing) {
        if (editing) focusRequester.requestFocus()
        setTextFieldValue(textFieldValue.copy(selection = TextRange(textFieldValue.text.length)))
    }

    PageSectionContent {
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (editing) {
                Column {
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .semantics { testTag = tt },
                        value = textFieldValue,
                        onValueChange = {
                            // update this text manually first so it doesn't show text that is too long for a split second
                            // but source of truth is still from text param
                            val newText = if (it.text.length <= 140) it.text else text
                            setTextFieldValue(it.copy(text = newText))
                            onTextChange(newText)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
                        cursorBrush = SolidColor(LocalContentColor.current)
                    )
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = (140 - text.length).toString(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } else {
                Text(
                    text = text.ifBlank { stringResource(R.string.entries_single_summary_placeholder) },
                    modifier = Modifier.semantics { testTag = tt })
            }
        }
    }
}