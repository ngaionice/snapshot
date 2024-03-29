package dev.ionice.snapshot.feature.entries.single.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.core.ui.components.PageSectionContent
import dev.ionice.snapshot.core.ui.screens.FunctionalityNotAvailableScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.feature.entries.R

@Composable
internal fun EntryTagSection(
    editing: Boolean,
    uiStateProvider: () -> TagsUiState,
    selectedTags: List<ContentTag>,
    onAddTag: (String) -> Unit,
    onSelectedTagsChange: (List<ContentTag>) -> Unit
) {
    val (displayedTagId, setDisplayedTagId) = remember { mutableStateOf<Long?>(null) }

    PageSectionContent {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val uiState = uiStateProvider()) {
                is TagsUiState.Loading -> {
                    LoadingScreen()
                }
                is TagsUiState.Error -> {
                    FunctionalityNotAvailableScreen("Failed to load tag data.")
                }
                is TagsUiState.Success -> {
                    val tagsMap = uiState.data.associateBy({ it.id }, { it })
                    if (editing) {
                        val selectedTagIds = selectedTags.map { it.tag.id }.toSet()
                        TagInserter(tags = uiState.data.filter { !selectedTagIds.contains(it.id) },
                            onAddTag = onAddTag,
                            onSelectTag = {
                                onSelectedTagsChange(
                                    selectedTags + ContentTag(tag = it)
                                )
                            })
                    }
                    TagDisplay(
                        tagsMap = tagsMap,
                        selectedTags = selectedTags,
                        displayedTagId = displayedTagId,
                        onClick = { if (editing) setDisplayedTagId(it) },
                        editing = editing
                    )
                    if (editing) {
                        if (displayedTagId == null) {
                            Text("Click on a tag to edit it!")
                        } else {
                            TagEditor(tagName = tagsMap[displayedTagId]!!.name,
                                tagEntry = selectedTags.find { it.tag.id == displayedTagId }!!,
                                onContentChange = { content ->
                                    onSelectedTagsChange(selectedTags.map {
                                        if (it.tag.id == displayedTagId) {
                                            it.copy(content = content)
                                        } else it
                                    })
                                },
                                onDelete = {
                                    onSelectedTagsChange(selectedTags.filter { it.tag.id != displayedTagId })
                                    setDisplayedTagId(null)
                                })
                        }
                    } else {
                        TagsContentDisplay(tagsMap = tagsMap, selectedTags = selectedTags)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TagDisplay(
    editing: Boolean,
    tagsMap: Map<Long, Tag>,
    selectedTags: List<ContentTag>,
    displayedTagId: Long?,
    onClick: (Long) -> Unit
) {
    if (selectedTags.isEmpty() && !editing) {
        Text(stringResource(R.string.single_tags_placeholder))
        return
    }
    val tt = stringResource(R.string.tt_single_tags_display)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { testTag = tt }) {
        selectedTags.forEach {
            InputChip(selected = displayedTagId == it.tag.id && editing,
                onClick = { onClick(it.tag.id) },
                label = { Text(tagsMap[it.tag.id]!!.name) })
        }
    }
}

@Composable
private fun TagInserter(
    tags: List<Tag>,
    onAddTag: (String) -> Unit,
    onSelectTag: (Tag) -> Unit
) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { setShowDialog(true) }) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.single_tag_add)
        )
    }
    if (showDialog) {
        TagInsertionDialog(tags = tags,
            onAddTag = onAddTag,
            onSelectTag = onSelectTag,
            onClose = { setShowDialog(false) })
    }
}

@Composable
private fun TagEditor(
    tagName: String,
    tagEntry: ContentTag,
    onContentChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    HorizontalDivider()
    Text(text = "#$tagName", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = tagEntry.content ?: "",
        onValueChange = onContentChange
    )
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onDelete) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.single_tag_delete)
        )
    }
}

@Composable
private fun TagsContentDisplay(
    tagsMap: Map<Long, Tag>,
    selectedTags: List<ContentTag>
) {
    val rendered = selectedTags.filter { !it.content.isNullOrEmpty() }
    if (rendered.isNotEmpty()) {
        HorizontalDivider()
        rendered.forEach {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "#${tagsMap[it.tag.id]!!.name}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(text = it.content!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagInsertionDialog(
    tags: List<Tag>,
    onAddTag: (String) -> Unit,
    onSelectTag: (Tag) -> Unit,
    onClose: () -> Unit
) {
    val (tabIndex, setTabIndex) = remember { mutableStateOf(0) }
    val (queued, setQueued) = remember { mutableStateOf<String?>(null) }
    val (selectedTag, setSelectedTag) = remember { mutableStateOf<Tag?>(null) }
    val (inputValue, setInputValue) = remember { mutableStateOf("") }
    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }

    val onConfirm = {
        if (tabIndex == 0) {
            onSelectTag(selectedTag!!)
            onClose()
        } else {
            // queue this item to be automatically selected when tag list gets updated
            setQueued(inputValue)
            onAddTag(inputValue)
        }
    }

    // if there is a queued item, then user has selected an item,
    // so select when it becomes available then close the dialog
    LaunchedEffect(tags) {
        if (queued != null) {
            val target = tags.find { it.name == queued }
            if (target != null) {
                onSelectTag(target)
                onClose()
            }
        }
    }

    AlertDialog(onDismissRequest = onClose, title = {
        Text("Add tag", style = MaterialTheme.typography.labelSmall)
    }, confirmButton = {
        Button(
            onClick = onConfirm,
            enabled = (selectedTag != null || inputValue.isNotEmpty()) && queued == null
        ) {
            Text(stringResource(dev.ionice.snapshot.core.ui.R.string.button_ok))
        }
    }, dismissButton = {
        TextButton(onClick = onClose) {
            Text(stringResource(dev.ionice.snapshot.core.ui.R.string.button_cancel))
        }
    }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SecondaryTabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = {
                    setTabIndex(0)
                    setSelectedTag(null)
                }, text = { Text(stringResource(R.string.single_tag_add_select_existing)) })
                Tab(selected = tabIndex == 1, onClick = {
                    setTabIndex(1)
                    setInputValue("")
                }, text = { Text(stringResource(R.string.single_tag_add_create_new)) })
            }
            when (tabIndex) {
                0 -> {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = setDropdownExpanded,
                        modifier = Modifier.testTag(stringResource(R.string.tt_single_tag_selector))
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = selectedTag?.name ?: "",
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(expanded = dropdownExpanded,
                            onDismissRequest = { setDropdownExpanded(false) }) {
                            tags.forEach { tag ->
                                DropdownMenuItem(text = { Text(tag.name) }, onClick = {
                                    setSelectedTag(tag)
                                    setDropdownExpanded(false)
                                })
                            }
                        }
                    }
                }
                1 -> {
                    if (queued == null) {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = setInputValue,
                            modifier = Modifier.testTag(stringResource(R.string.tt_single_tag_creator))
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    })
}