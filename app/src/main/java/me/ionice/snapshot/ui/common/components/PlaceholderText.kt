package me.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun PlaceholderText(textStyle: TextStyle, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .let {
                when (textStyle) {
                    MaterialTheme.typography.titleLarge -> it
                        .padding(vertical = 3.dp)
                        .height(22.dp)
                    MaterialTheme.typography.titleMedium -> it
                        .padding(vertical = 4.dp)
                        .height(16.dp)
                    MaterialTheme.typography.labelMedium -> it
                        .padding(vertical = 2.dp)
                        .height(12.dp)
                    MaterialTheme.typography.labelSmall -> it
                        .padding(vertical = 2.5.dp)
                        .height(11.dp)
                    else -> throw NotImplementedError()
                }
            }
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.fade(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {}
}