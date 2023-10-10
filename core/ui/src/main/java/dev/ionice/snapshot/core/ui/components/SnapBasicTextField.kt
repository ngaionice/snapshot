package dev.ionice.snapshot.core.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

/**
 * A BasicTextField that also shifts the cursor automatically to stay above IME as it grows.
 *
 * Issue Tracker: https://issuetracker.google.com/issues/266094055
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SnapBasicTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    cursorBrush: Brush = SolidColor(Color.Black)
) {
  val bringIntoViewRequester = remember { BringIntoViewRequester() }
  val scrollState = remember { ScrollState(0) }
  var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

  // Launched effect that updates scroll
  val selection = value.selection
  LaunchedEffect(selection) {
    if (enabled) {
      layoutResult?.let { layoutResult ->
        val (top, bottom) = layoutResult.cursorCoordinates(selection)
        bringIntoViewRequester.bringIntoView(Rect(0f, top, 0f, bottom))
      }
    }
  }

  Column(modifier = Modifier.verticalScroll(scrollState)) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        onTextLayout = { layoutResult = it },
        modifier = modifier.bringIntoViewRequester(bringIntoViewRequester),
        enabled = enabled,
        textStyle = textStyle,
        cursorBrush = cursorBrush
      )
  }
}

/** Returns the bottom and top y coordinate of the selected line. */
private fun TextLayoutResult.cursorCoordinates(selection: TextRange): Pair<Float, Float> {
  val currentLine =
      try {
        getLineForOffset(selection.end)
      } catch (ex: IllegalArgumentException) {
        System.err.println("Corrected Wrong Offset!")
        getLineForOffset(selection.end - 1)
      }
  val lineTop = getLineTop(currentLine)
  val lineBottom = getLineBottom(currentLine)
  return lineTop to lineBottom
}
