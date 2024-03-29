package dev.ionice.snapshot.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageSection(
    title: String,
    modifier: Modifier = Modifier,
    headerTextColor: Color? = null,
    headerBackgroundColor: Color? = null,
    headerAction: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        PageSectionHeader(
            title = title,
            textColor = headerTextColor,
            backgroundColor = headerBackgroundColor,
            action = headerAction
        )
        content()
    }
}

@Composable
fun PageSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
    backgroundColor: Color? = null,
    action: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(color = backgroundColor ?: Color.Transparent)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = textColor ?: MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.weight(1f))
        action()
    }
}

@Composable
fun PageSectionContent(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(bottom = 16.dp)) {
        content()
    }
}