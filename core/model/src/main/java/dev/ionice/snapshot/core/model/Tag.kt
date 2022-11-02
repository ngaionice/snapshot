package dev.ionice.snapshot.core.model

data class Tag(
    val id: Long,
    val name: String,
    val lastUsedAt: Long,
    val entries: List<TagEntry>
)

data class TagProperties(
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

data class TagEntry(
    val dayId: Long,
    val tagId: Long,
    val content: String? = null
)