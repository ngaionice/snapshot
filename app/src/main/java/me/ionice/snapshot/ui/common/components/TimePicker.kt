package me.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime

@Composable
fun TimePicker(initialTime: LocalTime, onSelectTime: (LocalTime) -> Unit) {

    var displayedDigits by remember {
        mutableStateOf(
            Pair(
                initialTime.hour.toString(),
                String.format("%02d", initialTime.minute)
                // need to pad values because distinctUntilChanged will not register the change otherwise
            )
        )
    }

    LaunchedEffect(key1 = displayedDigits) {
        println("value change: ${displayedDigits.second}")
        if (displayedDigits.first.isNotEmpty() && displayedDigits.second.isNotEmpty()) {
            onSelectTime(
                LocalTime.of(
                    Integer.parseInt(displayedDigits.first),
                    Integer.parseInt(displayedDigits.second)
                )
            )
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NumberInputBox(
            value = displayedDigits.first,
            onValueChange = { displayedDigits = displayedDigits.copy(first = it) },
            supportingText = "Hour",
            allowedRange = 0..23
        )
        Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
            Text(
                text = ":",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        NumberInputBox(
            value = displayedDigits.second,
            onValueChange = { displayedDigits = displayedDigits.copy(second = it) },
            supportingText = "Minute",
            allowedRange = 0..59
        )
    }
}

private fun formatValue(value: String): String = String.format("%02d", Integer.parseInt(value))

private fun filterText(oldText: String, newText: String, allowedRange: IntRange): String {
    try {
        if (newText.isEmpty()) return newText
        val number = Integer.parseInt(newText)
        return (if (number in allowedRange) newText else oldText).let {
            if (it.length > 2) it.substring(0, 2) else it
        }
    } catch (e: NumberFormatException) {
        return oldText
    }
}

@Composable
private fun NumberInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    allowedRange: IntRange,
    supportingText: String
) {
    var textValue by remember { mutableStateOf(formatValue(value)) }
    val focusRequester = remember { FocusRequester() }

    Column {
        Card(
            modifier = Modifier.width(100.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(contentAlignment = Alignment.Center) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            textValue = if (it.isFocused) "" else formatValue(value)
                        },
                    value = textValue,
                    onValueChange = { onValueChange(filterText(value, it, allowedRange)) },
                    textStyle = MaterialTheme.typography.displayLarge.copy(
                        color = LocalContentColor.current,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(LocalContentColor.current),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
        Text(text = supportingText, style = MaterialTheme.typography.labelMedium)
    }
    LaunchedEffect(key1 = value) {
        textValue = value
    }
    LaunchedEffect(key1 = Unit) {
        textValue = formatValue(value)
    }
}

@Preview
@Composable
private fun TimePickerPreview() {
    val value = MutableStateFlow(LocalTime.now())
    val display by value.collectAsState()
    TimePicker(initialTime = display, onSelectTime = { value.update { it } })
}