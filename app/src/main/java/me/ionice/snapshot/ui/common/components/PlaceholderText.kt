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
import me.ionice.snapshot.ui.common.titleMediumLarge

@Composable
fun PlaceholderText(textStyle: TextStyle, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .let {
                when (textStyle) {
                    titleMediumLarge() -> it.padding(vertical = 1.75.dp).height(20.dp)
                    MaterialTheme.typography.titleLarge -> it
                        .padding(vertical = 1.75.dp)
                        .height(22.dp)
                    MaterialTheme.typography.titleMedium -> it
                        .padding(vertical = 2.75.dp)
                        .height(16.dp)
                    MaterialTheme.typography.labelMedium -> it
                        .padding(vertical = 0.75.dp)
                        .height(12.dp)
                    MaterialTheme.typography.labelSmall -> it
                        .padding(vertical = 1.25.dp)
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