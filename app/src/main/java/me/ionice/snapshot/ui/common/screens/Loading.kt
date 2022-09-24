package me.ionice.snapshot.ui.common.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import me.ionice.snapshot.R

@Composable
fun LoadingScreen(message: String? = null, testTag: String? = null) {
    val tt = testTag ?: stringResource(R.string.tt_common_loading_screen)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics { this.testTag = tt },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        message?.let { Text(text = message) }
    }
}
