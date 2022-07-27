package me.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageSection(title: String, headerColor: Color? = null, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        PageSectionHeader(title = title, color = headerColor)
        content()
    }
}

@Composable
fun PageSectionHeader(title: String, color: Color? = null, action: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = color ?: MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.weight(1f))
        action?.invoke()
    }
}

@Composable
fun PageSectionContent(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(bottom = 16.dp)) {
        content()
    }
}