package com.example.reelstudio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_clones")
data class VoiceCloneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val tagline: String = "",
    val language: String = "Português (BR)",
    val sourceFileName: String = "voz-gravada.webm",
    val durationSeconds: Int = 12,
    val status: String = "ready",
    val createdAt: Long = System.currentTimeMillis()
)
