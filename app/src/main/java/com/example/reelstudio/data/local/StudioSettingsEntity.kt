package com.example.reelstudio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studio_settings")
data class StudioSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val workspaceName: String = "Northstar Studio",
    val defaultLanguage: String = "Português (BR)",
    val defaultFormat: String = "9:16 Portrait",
    val engine: String = "Remotion",
    val autosave: Boolean = true,
    val captions: Boolean = true,
    val captionStyle: String = "Karaoke"
)
