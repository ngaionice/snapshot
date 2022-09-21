package me.ionice.snapshot.ui.entries.single.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.model.TagProperties
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.ui.common.components.PageSectionContent
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen

@Composable
fun EntryTagSection(
    editing: Boolean,
    dayId: Long,
    uiStateProvider: () -> TagsUiState,
    selectedTags: List<TagEntry>,
    onAddTag: (String) -> Unit,
    onSelectedTagsChange: (List<TagEntry>) -> Unit
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
                        val selectedTagIds = selectedTags.map { it.tagId }.toSet()
                        TagInserter(
                            tags = uiState.data.filter { !selectedTagIds.contains(it.id) },
                            onAddTag = onAddTag,
                            onSelectTag = {
                                onSelectedTagsChange(
                                    selectedTags + TagEntry(dayId = dayId, tagId = it.id)
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
                                tagEntry = selectedTags.find { it.tagId == displayedTagId }!!,
                                onContentChange = { content ->
                                    onSelectedTagsChange(selectedTags.map {
                                        if (it.tagId == displayedTagId) {
                                            it.copy(content = content)
                                        } else it
                                    })
                                },
                                onDelete = {
                                    onSelectedTagsChange(selectedTags.filter { it.tagId != displayedTagId })
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagDisplay(
    editing: Boolean,
    tagsMap: Map<Long, TagProperties>,
    selectedTags: List<TagEntry>,
    displayedTagId: Long?,
    onClick: (Long) -> Unit
) {
    if (selectedTags.isEmpty() && !editing) {
        Text("Start adding tags!")
        return
    }
    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
        selectedTags.forEach {
            InputChip(selected = displayedTagId == it.tagId && editing,
                onClick = { onClick(it.tagId) },
                leadingIcon = if (!it.content.isNullOrEmpty()) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Notes, contentDescription = "has notes"
                        )
                    }
                } else null,
                label = { Text(tagsMap[it.tagId]!!.name) })
        }
    }
}

@Composable
private fun TagInserter(
    tags: List<TagProperties>, onAddTag: (String) -> Unit, onSelectTag: (TagProperties) -> Unit
) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { setShowDialog(true) }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add tag")
    }
    if (showDialog) {
        TagInsertionDialog(tags = tags,
            onAddTag = onAddTag,
            onSelectTag = onSelectTag,
            onClose = { setShowDialog(false) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagEditor(
    tagName: String, tagEntry: TagEntry, onContentChange: (String) -> Unit, onDelete: () -> Unit
) {
    Divider()
    Text(text = "#$tagName", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = tagEntry.content ?: "",
        onValueChange = onContentChange
    )
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onDelete) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete tag")
    }
}

@Composable
private fun TagsContentDisplay(tagsMap: Map<Long, TagProperties>, selectedTags: List<TagEntry>) {
    if (selectedTags.isNotEmpty()) {
        Divider()
    }
    selectedTags.filter { !it.content.isNullOrEmpty() }.forEach {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "#${tagsMap[it.tagId]!!.name}", style = MaterialTheme.typography.titleSmall)
            Text(text = it.content!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagInsertionDialog(
    tags: List<TagProperties>,
    onAddTag: (String) -> Unit,
    onSelectTag: (TagProperties) -> Unit,
    onClose: () -> Unit
) {
    val (tabIndex, setTabIndex) = remember { mutableStateOf(0) }
    val (queued, setQueued) = remember { mutableStateOf<String?>(null) }
    val (selectedTag, setSelectedTag) = remember { mutableStateOf<TagProperties?>(null) }
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
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onClose) {
            Text("Cancel")
        }
    }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = {
                    setTabIndex(0)
                    setSelectedTag(null)
                }, text = { Text("Select") })
                Tab(selected = tabIndex == 1, onClick = {
                    setTabIndex(1)
                    setInputValue("")
                }, text = { Text("Add new") })
            }
            when (tabIndex) {
                0 -> {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded, onExpandedChange = setDropdownExpanded
                    ) {
                        OutlinedTextField(
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
                        OutlinedTextField(value = inputValue, onValueChange = setInputValue)
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    })
}