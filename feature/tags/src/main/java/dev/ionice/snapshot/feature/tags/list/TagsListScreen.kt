package dev.ionice.snapshot.feature.tags.list

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.navigation.Navigator
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.feature.tags.R
import dev.ionice.snapshot.feature.tags.TagsUiState
import dev.ionice.snapshot.feature.tags.TagsViewModel
import dev.ionice.snapshot.feature.tags.components.PlaceholderList

@Composable
internal fun TagsListRoute(viewModel: TagsViewModel = hiltViewModel(), navigator: Navigator) {
    val uiState by viewModel.uiState.collectAsState()
    TagsListScreen(
        uiState = uiState,
        onSelectTag = { navigator.navigateToTag(it.id) },
        onBack = navigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
@Composable
internal fun TagsListScreen(uiState: TagsUiState, onSelectTag: (Tag) -> Unit, onBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.list_screen_header),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = { BackButton(onBack = onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        when (uiState) {
            is TagsUiState.Loading -> {
                PlaceholderList(
                    modifier = Modifier.padding(padding),
                    contentDesc = stringResource(R.string.cd_list_loading)
                )
            }
            is TagsUiState.Error -> {
                ErrorScreen(message = stringResource(R.string.list_load_failure))
            }
            is TagsUiState.Success -> {
                if (uiState.tagsList.isNotEmpty()) {
                    LazyColumn(contentPadding = padding) {
                        uiState.tagsList.sortedBy { it.name }.forEach {
                            item {
                                ListItem(
                                    headlineText = { Text(it.name) },
                                    modifier = Modifier
                                        .clickable { onSelectTag(it) }
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.padding(padding), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.list_empty_placeholder))
                    }
                }
            }
        }
    }
}