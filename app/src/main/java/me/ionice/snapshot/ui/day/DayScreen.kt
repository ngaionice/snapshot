package me.ionice.snapshot.ui.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.ionice.snapshot.ui.utils.Utils

@Composable
fun DayScreen(viewModel: DayViewModel = viewModel()) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Header(headerText = Utils.formatter.format(viewModel.date))
        }
    }
}

@Composable
fun Header(headerText: String, modifier: Modifier = Modifier) {
    Text(text = headerText, style = MaterialTheme.typography.headlineMedium)
}

@Preview
@Composable
fun DayScreenPreview() {
    DayScreen()
}