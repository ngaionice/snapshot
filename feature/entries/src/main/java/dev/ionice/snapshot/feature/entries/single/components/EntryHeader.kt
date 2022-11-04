package dev.ionice.snapshot.feature.entries.single.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import java.time.LocalDate

@Composable
internal fun EntryHeader(dayId: Long) {
    val date = LocalDate.ofEpochDay(dayId)

    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp, top = 60.dp)) {
        Text(
            text = date.format(Utils.fullDateMonthFormatter),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = date.year.toString(), style = MaterialTheme.typography.titleMedium)
    }
}