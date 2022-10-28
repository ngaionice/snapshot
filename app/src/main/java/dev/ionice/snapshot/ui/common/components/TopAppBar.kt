package dev.ionice.snapshot.ui.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String = "snapshot",
    actions: @Composable @ExtensionFunctionType RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        modifier = modifier,
        actions = actions,
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    )
}

@Preview
@Composable
private fun TopAppBarPreview() {
    TopAppBar {
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}