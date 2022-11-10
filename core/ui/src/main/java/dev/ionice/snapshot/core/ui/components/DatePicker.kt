package dev.ionice.snapshot.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import dev.ionice.snapshot.core.ui.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    date: LocalDate?,
    onSelectDate: (LocalDate) -> Unit,
    showErrorIcon: Boolean,
    setErrorMessage: (String) -> Unit,
    allowFuture: Boolean = true,
    label: String? = null,
    contentDescription: String? = null
) {
    val (dateString, setDateString) = remember { mutableStateOf(date?.format(formatter) ?: "") }
    val errorMessage = stringResource(R.string.date_picker_range_error_msg)
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
        val cd = contentDescription ?: stringResource(R.string.cd_date_picker_text_field)
        OutlinedTextField(
            modifier = Modifier.semantics { this.contentDescription = cd },
            value = dateString,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(label ?: stringResource(R.string.date_picker_default_label)) },
            placeholder = { Text("yyyy-mm-dd", style = MaterialTheme.typography.labelSmall) },
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

