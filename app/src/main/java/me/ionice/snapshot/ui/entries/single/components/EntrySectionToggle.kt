package me.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.ui.common.components.PageSectionContent
import me.ionice.snapshot.ui.entries.single.EntrySection

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
                            Icon(
                                imageVector = it.icon,
                                contentDescription = stringResource(it.descriptionResId)
                            )
                            Text(text = it.label)
                        }
                    }
                } else {
                    FilledTonalIconButton(onClick = { onSelect(it) }) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.descriptionResId)
                        )
                    }
                }
            }
        }
    }

}