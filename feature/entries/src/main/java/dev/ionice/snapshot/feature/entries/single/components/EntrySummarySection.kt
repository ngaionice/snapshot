package dev.ionice.snapshot.feature.entries.single.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import dev.ionice.snapshot.core.ui.components.PageSectionContent
import dev.ionice.snapshot.core.ui.components.SnapBasicTextField
import dev.ionice.snapshot.feature.entries.R

@Composable
internal fun EntrySummarySection(editing: Boolean, text: String, onTextChange: (String) -> Unit) {
  val focusRequester = remember { FocusRequester() }
  val (textFieldValue, setTextFieldValue) = remember { mutableStateOf(TextFieldValue(text)) }

  val tt = stringResource(R.string.tt_single_summary)

  LaunchedEffect(key1 = text) { setTextFieldValue(textFieldValue.copy(text = text)) }

  LaunchedEffect(key1 = editing) {
    if (editing) focusRequester.requestFocus()
    setTextFieldValue(textFieldValue.copy(selection = TextRange(textFieldValue.text.length)))
  }

  PageSectionContent {
    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
      SnapBasicTextField(
        value = textFieldValue,
        onValueChange = {
          setTextFieldValue(it)
          onTextChange(it.text)
        },
        textStyle =
        MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(LocalContentColor.current),
        enabled = editing,
        modifier =
        Modifier.fillMaxWidth().focusRequester(focusRequester).semantics { testTag = tt })
    }
  }
}
