package com.example.reelstudio

import android.app.Application
import com.example.reelstudio.data.local.AppDatabase
import com.example.reelstudio.data.repository.ReelStudioRepository

class ReelStudioApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ReelStudioRepository(database.projectDao()) }
}
