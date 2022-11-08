package dev.ionice.snapshot.core.model

data class Tag(
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

data class ContentTag(
    val tag: Tag,
    val content: String? = null
)
