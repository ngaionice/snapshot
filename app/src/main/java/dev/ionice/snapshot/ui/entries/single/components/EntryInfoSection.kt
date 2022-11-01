package dev.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.RelativeTime

@Composable
fun EntryInfoSection(lastModifiedAt: Long) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val relativeTimeString = RelativeTime.getPastDuration(lastModifiedAt)
        Text(
            text = "Last modified $relativeTimeString",
            style = MaterialTheme.typography.labelLarge
        )
    }
}