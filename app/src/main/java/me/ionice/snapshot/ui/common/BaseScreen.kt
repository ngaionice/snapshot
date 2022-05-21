package me.ionice.snapshot.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseScreen(headerText: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Header(
            headerText = headerText,
            modifier = Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
        )
        content()
    }
}

@Composable
fun Header(headerText: String, modifier: Modifier = Modifier) {
    Text(text = headerText, style = MaterialTheme.typography.displaySmall, modifier = modifier)
}
