package dev.ionice.snapshot.core.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.ionice.snapshot.core.ui.R

@Composable
fun FunctionalityNotAvailableScreen(message: String? = null) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = message ?: stringResource(R.string.common_functionality_soon),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}