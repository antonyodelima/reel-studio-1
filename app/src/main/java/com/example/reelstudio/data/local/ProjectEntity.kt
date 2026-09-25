package com.example.reelstudio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String = "Reel",
    val preset: String = "Lançamento de produto",
    val status: String = "draft", // "draft", "rendering", "ready"
    val scenesCount: Int = 5,
    val duration: String = "00:28",
    val updated: String = "Agora",
    val color: String = "coral",
    val format: String = "9:16 Portrait",
    val engine: String = "Remotion",
    val soundtrack: String = "Brilho ambiente",
    val voice: String = "Sofia",
    val exportedMp4Uri: String? = null,
    val exportedMp4SizeFormatted: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
