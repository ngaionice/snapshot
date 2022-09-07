package me.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun EntrySummarySection(editing: Boolean, text: String, onTextChange: (String) -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        if (editing) {
            Column {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { if (it.length <= 140) onTextChange(it) },
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
            Text(text = text.ifBlank { "Start writing your summary!" })
        }
    }
}