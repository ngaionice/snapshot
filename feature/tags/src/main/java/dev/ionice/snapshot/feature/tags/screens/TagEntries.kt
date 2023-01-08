package dev.ionice.snapshot.feature.tags.screens

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.navigation.Navigator
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.feature.tags.R
import dev.ionice.snapshot.feature.tags.TagsUiState
import dev.ionice.snapshot.feature.tags.TagsViewModel
import dev.ionice.snapshot.feature.tags.components.PlaceholderList
import java.time.LocalDate

@Composable
internal fun TagsSingleRoute(
    viewModel: TagsViewModel = hiltViewModel(),
    tagId: Long,
    navigator: Navigator
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(tagId) { viewModel.loadTag(tagId) }
    TagsSingleScreen(
        uiState = uiState,
        onSelectEntry = navigator::navigateToEntry,
        onBack = navigator::navigateBack
    )
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearTag()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
@Composable
internal fun TagsSingleScreen(
    uiState: TagsUiState,
    onSelectEntry: (Long) -> Unit,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
        LargeTopAppBar(
            title = {
                Text(
                    text = if (uiState is TagsUiState.Success) uiState.selectedTag?.first?.name
                        ?: "" else "",
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
                    contentDesc = stringResource(R.string.entries_load_loading)
                )
            }
            is TagsUiState.Error -> {
                ErrorScreen(message = stringResource(R.string.entries_load_failure))
            }
            is TagsUiState.Success -> {
                val tagData = uiState.selectedTag
                if (tagData == null || tagData.second.isEmpty()) {
                    Box(modifier = Modifier.padding(padding), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.no_entries_found))
                    }
                } else {
                    SuccessScreen(
                        data = uiState.selectedTag,
                        modifier = Modifier.padding(padding),
                        onSelectEntry = onSelectEntry
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessScreen(
    data: Pair<Tag, List<Day>>,
    modifier: Modifier = Modifier,
    onSelectEntry: (Long) -> Unit
) {
    val (tagData, entries) = data
    val currYear = LocalDate.now().year
    val entryContentDesc = stringResource(R.string.cd_tag_entry)
    Column(modifier = modifier) {
        LazyColumn {
            entries.forEach { day ->
                item {
                    val date = LocalDate.ofEpochDay(day.id)
                    val content = day.tags.find { tag -> tag.tag.id == tagData.id }?.content
                    val headlineText = @Composable {
                        Text(
                            date.let {
                                it.format(
                                    if (it.year == currYear) Utils.shortDateFormatter else Utils.dateFormatter,
                                )
                            },
                        )
                    }
                    val itemModifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectEntry(day.id) }
                        .padding(horizontal = 8.dp)
                        .semantics {
                            contentDescription =
                                String.format(
                                    entryContentDesc,
                                    date.format(Utils.dateFormatter)
                                )
                        }
                    if (content != null) {
                        ListItem(
                            headlineText = headlineText,
                            supportingText = { Text(text = content) },
                            modifier = itemModifier
                        )
                    } else {
                        ListItem(headlineText = headlineText, modifier = itemModifier)
                    }
                }
            }
        }
    }
}