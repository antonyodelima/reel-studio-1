package com.example.reelstudio.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scenes",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class SceneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: String,
    val orderIndex: Int,
    val label: String,
    val title: String,
    val caption: String,
    val duration: String = "00:05",
    val durationSeconds: Int = 5,
    val accent: String = "coral",
    val mediaType: String = "Gradient",
    val captionStyle: String = "Karaoke",
    val transition: String = "Dissolve",
    val isLocked: Boolean = false,
    val trimStartSeconds: Float = 0.0f,
    val trimEndSeconds: Float = 5.0f,
    val originalDurationSeconds: Float = 5.0f,
    val voiceId: String? = null,
    val voiceName: String? = null,
    val voiceLanguage: String? = null,
    val voiceAudioUrl: String? = null,
    val voiceStatus: String = "none" // "none", "generating", "ready"
)
