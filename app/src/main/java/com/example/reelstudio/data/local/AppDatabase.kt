package com.example.reelstudio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        SceneEntity::class,
        VoiceCloneEntity::class,
        StudioSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reel_studio_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database.projectDao())
                    }
                }
            }
        }

        suspend fun prepopulateDatabase(dao: ProjectDao) {
            dao.clearProjects()

            val p1 = ProjectEntity(
                id = "product-launch",
                name = "Lançamento de produto / Aurora",
                type = "Reel",
                preset = "Lançamento de produto",
                status = "ready",
                scenesCount = 5,
                duration = "00:32",
                updated = "2 min atrás",
                color = "coral",
                format = "9:16 Portrait"
            )
            val p2 = ProjectEntity(
                id = "founder-story",
                name = "História da fundadora — do zero",
                type = "Short",
                preset = "Impacto do criador",
                status = "draft",
                scenesCount = 5,
                duration = "00:24",
                updated = "Ontem",
                color = "violet",
                format = "9:16 Portrait"
            )
            val p3 = ProjectEntity(
                id = "data-story",
                name = "Por que equipes entregam mais rápido",
                type = "Explainer",
                preset = "História com dados",
                status = "rendering",
                scenesCount = 5,
                duration = "00:48",
                updated = "Ontem",
                color = "mint",
                format = "1:1 Square"
            )

            dao.insertProject(p1)
            dao.insertProject(p2)
            dao.insertProject(p3)

            val starterScenesP1 = listOf(
                SceneEntity(
                    projectId = "product-launch",
                    orderIndex = 0,
                    label = "Gancho",
                    title = "O jeito antigo acabou.",
                    caption = "O jeito antigo acabou.",
                    duration = "00:04",
                    durationSeconds = 4,
                    accent = "coral",
                    voiceStatus = "ready",
                    voiceName = "Sofia"
                ),
                SceneEntity(
                    projectId = "product-launch",
                    orderIndex = 1,
                    label = "Contexto",
                    title = "Ideias avançam na velocidade do seu fluxo.",
                    caption = "Ideias avançam na velocidade do seu fluxo.",
                    duration = "00:06",
                    durationSeconds = 6,
                    accent = "violet",
                    voiceStatus = "ready",
                    voiceName = "Sofia"
                ),
                SceneEntity(
                    projectId = "product-launch",
                    orderIndex = 2,
                    label = "Prova",
                    title = "Um briefing. Todos os formatos.",
                    caption = "Um briefing. Todos os formatos.",
                    duration = "00:05",
                    durationSeconds = 5,
                    accent = "mint",
                    voiceStatus = "ready",
                    voiceName = "Sofia"
                ),
                SceneEntity(
                    projectId = "product-launch",
                    orderIndex = 3,
                    label = "Recurso",
                    title = "Crie, refine, publique.",
                    caption = "Crie, refine, publique.",
                    duration = "00:07",
                    durationSeconds = 7,
                    accent = "sky",
                    voiceStatus = "ready",
                    voiceName = "Sofia"
                ),
                SceneEntity(
                    projectId = "product-launch",
                    orderIndex = 4,
                    label = "CTA",
                    title = "Faça seu próximo reel parecer inevitável.",
                    caption = "Faça seu próximo reel parecer inevitável.",
                    duration = "00:06",
                    durationSeconds = 6,
                    accent = "amber",
                    voiceStatus = "ready",
                    voiceName = "Sofia"
                )
            )

            dao.insertScenes(starterScenesP1)

            dao.saveSettings(
                StudioSettingsEntity(
                    workspaceName = "Northstar Studio",
                    defaultLanguage = "Português (BR)",
                    defaultFormat = "9:16 Portrait",
                    engine = "Remotion",
                    autosave = true,
                    captions = true,
                    captionStyle = "Karaoke"
                )
            )

            dao.insertVoiceClone(
                VoiceCloneEntity(
                    name = "Voz da Marca Aurora",
                    tagline = "Confiante e calorosa",
                    language = "Português (BR)",
                    sourceFileName = "amostra-estudio.wav",
                    durationSeconds = 18,
                    status = "ready"
                )
            )
        }
    }
}
