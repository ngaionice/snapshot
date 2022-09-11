package me.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.PageSectionContent
import me.ionice.snapshot.ui.entries.single.EntrySection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrySectionToggle(selectedProvider: () -> EntrySection, onSelect: (EntrySection) -> Unit) {
    val selected = selectedProvider()

    PageSectionContent {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EntrySection.values().forEach {
                if (it == selected) {
                    Button(onClick = {}) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = it.icon, contentDescription = it.description)
                            Text(text = it.description)
                        }
                    }
                } else {
                    FilledTonalIconButton(onClick = { onSelect(it) }) {
                        Icon(imageVector = it.icon, contentDescription = it.description)
                    }
                }
            }
        }
    }

}