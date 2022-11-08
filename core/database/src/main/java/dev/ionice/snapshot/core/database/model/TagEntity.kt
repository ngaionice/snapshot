package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Tag

data class TagEntity(
    @Embedded
    val properties: TagPropertiesEntity,
    @Relation(parentColumn = "id", entityColumn = "tagId")
    val entries: List<TagEntryEntity>
)

@Entity(tableName = "Tag")
data class TagPropertiesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

fun TagPropertiesEntity.toExternalModel(): Tag = Tag(id, name, lastUsedAt)

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

@Fts4(contentEntity = TagEntryEntity::class)
@Entity
data class TagEntryFts(
    val dayId: Long,
    val tagId: Long,
    val content: String?
)
