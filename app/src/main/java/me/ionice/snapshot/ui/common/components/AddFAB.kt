package me.ionice.snapshot.ui.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun AddFAB(onClick: () -> Unit, description: String) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = description)
    }
}