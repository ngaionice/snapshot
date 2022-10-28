package dev.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import dev.ionice.snapshot.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    date: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    showErrorIcon: Boolean,
    setErrorMessage: (String) -> Unit,
    allowFuture: Boolean = true
) {
    val (dateString, setDateString) = remember { mutableStateOf(date.format(formatter)) }
    val errorMessage = stringResource(R.string.common_datepicker_error_range)
    val onValueChange: (String) -> Unit = {
        val cleaned = trimAndClean(it)
        setDateString(cleaned)
        if (cleaned.length == 8) {
            try {
                val newDate = LocalDate.parse(cleaned, formatter)
                if (!allowFuture && newDate.isAfter(LocalDate.now())) {
                    setErrorMessage(errorMessage)
                } else {
                    onSelectDate(newDate)
                    setErrorMessage("")
                }
            } catch (e: DateTimeParseException) {
                setErrorMessage("Invalid format. Use yyyy-mm-dd")
            }
        }
    }
    Column {
        OutlinedTextField(
            modifier = Modifier.testTag(stringResource(R.string.tt_common_date_picker)),
            value = dateString,
            onValueChange = onValueChange,
            label = { Text("Date") },
            placeholder = { Text("yyyy-mm-dd") },
            trailingIcon = {
                if (showErrorIcon) {
                    Icon(imageVector = Icons.Filled.Error, contentDescription = "error")
                }
            },
            visualTransformation = DateTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

private class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val cleaned = trimAndClean(text.text)
        var out = ""
        cleaned.indices.forEach { idx ->
            out += cleaned[idx]
            if (idx == 3 || idx == 5) out += '-'
        }

        // XXXX-XX-XX
        val dateTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), dateTranslator)
    }
}

private fun trimAndClean(uncleaned: String): String {
    val trimmed = if (uncleaned.length >= 8) uncleaned.substring(0..7) else uncleaned
    return trimmed.takeWhile { Character.isDigit(it) }
}

private val formatter = DateTimeFormatter.ofPattern("uuuuMMdd")

