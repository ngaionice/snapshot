package me.ionice.snapshot.ui.common.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    headerText: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = headerText,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },
                navigationIcon = {
                    if (navigationIcon != null) {
                        navigationIcon()
                    }
                },
                actions = {
                    if (actions != null) {
                        actions()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (floatingActionButton != null) {
                floatingActionButton()
            }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(snackbarHostState) })
    {
        Box(modifier = modifier.padding(it)) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    headerBar: @Composable () -> Unit,
    floatingActionButton: (@Composable () -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            if (floatingActionButton != null) {
                floatingActionButton()
            }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(snackbarHostState) })
    {
        Column(
            modifier = modifier
                .padding(it)
        ) {
            headerBar()
            content()
        }
    }
}

@Composable
fun Header(headerText: String, modifier: Modifier = Modifier) {
    Text(text = headerText, style = MaterialTheme.typography.displaySmall, modifier = modifier)
}
