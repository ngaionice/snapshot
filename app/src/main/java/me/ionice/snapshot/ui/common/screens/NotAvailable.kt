package me.ionice.snapshot.ui.common.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R

@Composable
fun FunctionalityNotYetAvailableScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.common_functionality_soon),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FunctionalityNotAvailableScreen(reason: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.common_functionality_na_with_reason, reason),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}