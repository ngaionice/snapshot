package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.model.TagEntry
import dev.ionice.snapshot.core.model.TagProperties

data class TagEntity(
    @Embedded
    val properties: TagPropertiesEntity,
    @Relation(parentColumn = "id", entityColumn = "tagId")
    val entries: List<TagEntryEntity>
)

fun TagEntity.toExternalModel(): Tag {
    val (id, name, lastUsedAt) = properties
    return Tag(id, name, lastUsedAt, entries.map { it.toExternalModel() })
}

@Entity(tableName = "Tag")
data class TagPropertiesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

fun TagPropertiesEntity.toExternalModel(): TagProperties = TagProperties(id, name, lastUsedAt)

@Entity(
    tableName = "TagEntry",
    primaryKeys = ["dayId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = DayProperties::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagPropertiesEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId"), Index("tagId")]
)
data class TagEntryEntity(
    val dayId: Long,
    val tagId: Long,
    val content: String? = null
)

fun TagEntryEntity.toExternalModel(): TagEntry = TagEntry(dayId, tagId, content)

@Fts4(contentEntity = TagEntryEntity::class)
@Entity
data class TagEntryFts(
    val dayId: Long,
    val tagId: Long,
    val content: String?
)
