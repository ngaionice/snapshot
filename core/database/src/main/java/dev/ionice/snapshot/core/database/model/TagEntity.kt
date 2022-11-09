package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Tag

@Entity(tableName = "Tag")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

fun TagEntity.toExternalModel(): Tag = Tag(id, name, lastUsedAt)

@Fts4(contentEntity = DayTagCrossRef::class)
@Entity
data class TagEntryFts(
    val dayId: Long,
    val tagId: Long,
    val content: String?
)
