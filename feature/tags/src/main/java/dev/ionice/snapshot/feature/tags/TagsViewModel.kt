package dev.ionice.snapshot.feature.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.data.repository.TagRepository
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class TagsViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    tagRepository: TagRepository
) : ViewModel() {

    private val tagsFlow = tagRepository.getAllFlow().asResult()

    private val tagId = MutableStateFlow<Long?>(null)

    private val selectedTagFlow =
        tagId.flatMapLatest { it?.let { tagRepository.getFlow(it) } ?: emptyFlow() }.asResult()

    private val selectedTagEntriesFlow = tagId.flatMapLatest {
        it?.let { dayRepository.getListFlowByTag(it) } ?: emptyFlow()
    }.asResult()

    val uiState: StateFlow<TagsUiState> = combine(
        tagsFlow,
        selectedTagFlow,
        selectedTagEntriesFlow
    ) { allTagsResult, tagResult, tagEntriesResult ->
        val tagDataLoaded =
            tagId.value == null || tagResult is Result.Success && tagEntriesResult is Result.Success
        if (allTagsResult is Result.Success && tagDataLoaded) {
            if (tagResult is Result.Success && tagEntriesResult is Result.Success) {
                TagsUiState.Success(
                    tagsList = allTagsResult.data,
                    selectedTag = tagResult.data?.let { Pair(it, tagEntriesResult.data) })
            } else {
                TagsUiState.Success(tagsList = allTagsResult.data, selectedTag = null)
            }
        } else if (allTagsResult is Result.Error || tagResult is Result.Error || tagEntriesResult is Result.Error) {
            TagsUiState.Error
        } else {
            TagsUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TagsUiState.Loading
    )

    fun loadTag(tagId: Long) {
        this.tagId.update { tagId }
    }

    fun clearTag() {
        this.tagId.update { null }
    }
}

internal sealed interface TagsUiState {
    object Loading : TagsUiState
    object Error : TagsUiState
    data class Success(
        val tagsList: List<Tag>,
        val selectedTag: Pair<Tag, List<Day>>?
    ) : TagsUiState
}