package dev.ionice.snapshot.ui.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.ionice.snapshot.R

@Composable
fun BackButton(onBack: () -> Unit, contentDesc: String? = null) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = contentDesc ?: stringResource(R.string.common_back)
        )
    }
}