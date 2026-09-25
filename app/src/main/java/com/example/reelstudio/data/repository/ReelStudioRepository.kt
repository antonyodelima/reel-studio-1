package com.example.reelstudio.data.repository

import com.example.reelstudio.data.local.AppDatabase
import com.example.reelstudio.data.local.ProjectDao
import com.example.reelstudio.data.local.ProjectEntity
import com.example.reelstudio.data.local.SceneEntity
import com.example.reelstudio.data.local.StudioSettingsEntity
import com.example.reelstudio.data.local.VoiceCloneEntity
import com.example.reelstudio.data.model.ReelDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class ReelStudioRepository(private val dao: ProjectDao) {
    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val allVoiceClones: Flow<List<VoiceCloneEntity>> = dao.getAllVoiceClones()
    val settings: Flow<StudioSettingsEntity?> = dao.getSettings()

    fun getProject(id: String): Flow<ProjectEntity?> = dao.getProjectById(id)
    fun getScenes(projectId: String): Flow<List<SceneEntity>> = dao.getScenesForProject(projectId)

    suspend fun createProject(
        name: String,
        preset: String = "Lançamento de produto",
        brief: String = "",
        type: String = "Reel",
        format: String = "9:16 Portrait"
    ): ProjectEntity = withContext(Dispatchers.IO) {
        val id = "proj-" + UUID.randomUUID().toString().take(8)
        val color = when {
            preset.contains("Criador", ignoreCase = true) || preset.contains("Creator", ignoreCase = true) -> "violet"
            preset.contains("Dados", ignoreCase = true) || preset.contains("Data", ignoreCase = true) -> "mint"
            preset.contains("Editorial", ignoreCase = true) -> "sky"
            preset.contains("Demo", ignoreCase = true) -> "amber"
            preset.contains("Marca", ignoreCase = true) -> "plum"
            else -> "coral"
        }

        val project = ProjectEntity(
            id = id,
            name = name,
            type = type,
            preset = preset,
            status = "draft",
            scenesCount = if (brief.isNotBlank()) 5 else 3,
            duration = if (brief.isNotBlank()) "00:25" else "00:15",
            updated = "Agora",
            color = color,
            format = format
        )
        dao.insertProject(project)

        val scenes = if (brief.isNotBlank()) {
            listOf(
                SceneEntity(projectId = id, orderIndex = 0, label = "Gancho", title = name, caption = name, duration = "00:04", durationSeconds = 4, accent = color),
                SceneEntity(projectId = id, orderIndex = 1, label = "Contexto", title = brief.take(60), caption = brief.take(60), duration = "00:06", durationSeconds = 6, accent = "violet"),
                SceneEntity(projectId = id, orderIndex = 2, label = "Prova", title = "Resultados mensuráveis desde o primeiro dia.", caption = "Resultados mensuráveis desde o primeiro dia.", duration = "00:05", durationSeconds = 5, accent = "mint"),
                SceneEntity(projectId = id, orderIndex = 3, label = "Recurso", title = "Fluxo intuitivo, sem atrito e com foco.", caption = "Fluxo intuitivo, sem atrito e com foco.", duration = "00:05", durationSeconds = 5, accent = "sky"),
                SceneEntity(projectId = id, orderIndex = 4, label = "CTA", title = "Comece agora e transforme seu conteúdo.", caption = "Comece agora e transforme seu conteúdo.", duration = "00:05", durationSeconds = 5, accent = "amber")
            )
        } else {
            listOf(
                SceneEntity(projectId = id, orderIndex = 0, label = "Gancho", title = "O jeito antigo acabou.", caption = "O jeito antigo acabou.", duration = "00:04", durationSeconds = 4, accent = color),
                SceneEntity(projectId = id, orderIndex = 1, label = "Recurso", title = "Crie seu próximo reel com clareza.", caption = "Crie seu próximo reel com clareza.", duration = "00:06", durationSeconds = 6, accent = "sky"),
                SceneEntity(projectId = id, orderIndex = 2, label = "CTA", title = "Faça seu próximo vídeo parecer inevitável.", caption = "Faça seu próximo vídeo parecer inevitável.", duration = "00:05", durationSeconds = 5, accent = "amber")
            )
        }
        dao.insertScenes(scenes)
        project
    }

    suspend fun updateProject(project: ProjectEntity) = withContext(Dispatchers.IO) {
        dao.updateProject(project)
    }

    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        dao.deleteScenesForProject(id)
        dao.deleteProjectById(id)
    }

    suspend fun duplicateProject(projectId: String): ProjectEntity? = withContext(Dispatchers.IO) {
        val original = dao.getProjectByIdOnce(projectId) ?: return@withContext null
        val newId = "proj-" + UUID.randomUUID().toString().take(8)
        val duplicated = original.copy(
            id = newId,
            name = "${original.name} (Cópia)",
            status = "draft",
            updated = "Agora",
            timestamp = System.currentTimeMillis()
        )
        dao.insertProject(duplicated)
        val originalScenes = dao.getScenesForProjectOnce(projectId)
        val newScenes = originalScenes.map { scene ->
            scene.copy(id = 0, projectId = newId)
        }
        dao.insertScenes(newScenes)
        duplicated
    }

    suspend fun getScenesSync(projectId: String): List<SceneEntity> = withContext(Dispatchers.IO) {
        dao.getScenesForProjectOnce(projectId)
    }

    suspend fun addScene(projectId: String, currentCount: Int): SceneEntity = withContext(Dispatchers.IO) {
        val accents = listOf("coral", "violet", "mint", "sky", "amber", "plum")
        val accent = accents[currentCount % accents.size]
        val labels = listOf("Gancho", "Contexto", "Prova", "Recurso", "CTA", "Momento")
        val label = labels[currentCount % labels.size]

        val newScene = SceneEntity(
            projectId = projectId,
            orderIndex = currentCount,
            label = label,
            title = "Nova cena ${currentCount + 1}",
            caption = "Adicione o próximo momento da sua história.",
            duration = "00:05",
            durationSeconds = 5,
            accent = accent
        )
        val id = dao.insertScene(newScene)
        val updated = newScene.copy(id = id)

        // Update project scene count
        dao.getProjectByIdOnce(projectId)?.let { proj ->
            val scenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = scenes.sumOf { it.durationSeconds }
            val mins = totalSeconds / 60
            val secs = totalSeconds % 60
            val durStr = String.format("%02d:%02d", mins, secs)
            dao.updateProject(proj.copy(scenesCount = scenes.size, duration = durStr, updated = "Agora"))
        }

        updated
    }

    suspend fun updateScene(scene: SceneEntity) = withContext(Dispatchers.IO) {
        dao.updateScene(scene)
    }

    suspend fun updateSceneDuration(sceneId: Long, projectId: String, seconds: Int) = withContext(Dispatchers.IO) {
        val safeSeconds = seconds.coerceIn(1, 60)
        val scenes = dao.getScenesForProjectOnce(projectId)
        scenes.find { it.id == sceneId }?.let { scene ->
            val durStr = String.format("%02d:%02d", safeSeconds / 60, safeSeconds % 60)
            dao.updateScene(
                scene.copy(
                    durationSeconds = safeSeconds,
                    duration = durStr,
                    trimStartSeconds = 0f,
                    trimEndSeconds = safeSeconds.toFloat()
                )
            )
            val updatedScenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = updatedScenes.sumOf { it.durationSeconds }
            val pDurStr = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
            dao.getProjectByIdOnce(projectId)?.let { proj ->
                dao.updateProject(proj.copy(duration = pDurStr, updated = "Agora"))
            }
        }
    }

    suspend fun updateSceneCaption(sceneId: Long, projectId: String, caption: String) = withContext(Dispatchers.IO) {
        val scenes = dao.getScenesForProjectOnce(projectId)
        scenes.find { it.id == sceneId }?.let { scene ->
            dao.updateScene(scene.copy(caption = caption))
        }
    }

    suspend fun deleteScene(sceneId: Long, projectId: String) = withContext(Dispatchers.IO) {
        dao.deleteScene(sceneId)
        dao.getProjectByIdOnce(projectId)?.let { proj ->
            val scenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = scenes.sumOf { it.durationSeconds }
            val mins = totalSeconds / 60
            val secs = totalSeconds % 60
            val durStr = String.format("%02d:%02d", mins, secs)
            dao.updateProject(proj.copy(scenesCount = scenes.size, duration = durStr, updated = "Agora"))
        }
    }

    suspend fun updateProjectStatus(projectId: String, status: String) = withContext(Dispatchers.IO) {
        dao.getProjectByIdOnce(projectId)?.let { proj ->
            dao.updateProject(proj.copy(status = status, updated = "Agora"))
        }
    }

    suspend fun saveExportedMp4(projectId: String, uri: String, sizeFormatted: String) = withContext(Dispatchers.IO) {
        dao.getProjectByIdOnce(projectId)?.let { proj ->
            dao.updateProject(
                proj.copy(
                    status = "ready",
                    exportedMp4Uri = uri,
                    exportedMp4SizeFormatted = sizeFormatted,
                    updated = "Agora"
                )
            )
        }
    }

    suspend fun reorderScenes(projectId: String, fromIndex: Int, toIndex: Int) = withContext(Dispatchers.IO) {
        val scenes = dao.getScenesForProjectOnce(projectId).toMutableList()
        if (fromIndex in scenes.indices && toIndex in scenes.indices) {
            val moved = scenes.removeAt(fromIndex)
            scenes.add(toIndex, moved)
            scenes.forEachIndexed { idx, scene ->
                dao.updateScene(scene.copy(orderIndex = idx))
            }
        }
    }

    suspend fun trimScene(sceneId: Long, projectId: String, startSec: Float, endSec: Float) = withContext(Dispatchers.IO) {
        val scenes = dao.getScenesForProjectOnce(projectId)
        scenes.find { it.id == sceneId }?.let { scene ->
            val trimmedDurationSec = (endSec - startSec).toInt().coerceAtLeast(1)
            val mins = trimmedDurationSec / 60
            val secs = trimmedDurationSec % 60
            val durStr = String.format("%02d:%02d", mins, secs)

            dao.updateScene(
                scene.copy(
                    trimStartSeconds = startSec,
                    trimEndSeconds = endSec,
                    duration = durStr,
                    durationSeconds = trimmedDurationSec
                )
            )

            // Recalculate project duration
            val updatedScenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = updatedScenes.sumOf { (it.trimEndSeconds - it.trimStartSeconds).toInt().coerceAtLeast(1) }
            val pMins = totalSeconds / 60
            val pSecs = totalSeconds % 60
            val pDurStr = String.format("%02d:%02d", pMins, pSecs)
            dao.getProjectByIdOnce(projectId)?.let { proj ->
                dao.updateProject(proj.copy(duration = pDurStr, updated = "Agora"))
            }
        }
    }

    suspend fun duplicateScene(projectId: String, sceneId: Long) = withContext(Dispatchers.IO) {
        val scenes = dao.getScenesForProjectOnce(projectId)
        val target = scenes.find { it.id == sceneId } ?: return@withContext
        val newOrder = target.orderIndex + 1

        val duplicate = target.copy(
            id = 0,
            orderIndex = newOrder,
            label = "${target.label} (cópia)",
            title = target.title
        )

        // Shift subsequent scenes
        scenes.filter { it.orderIndex >= newOrder }.forEach {
            dao.updateScene(it.copy(orderIndex = it.orderIndex + 1))
        }

        dao.insertScene(duplicate)

        dao.getProjectByIdOnce(projectId)?.let { proj ->
            val updatedScenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = updatedScenes.sumOf { it.durationSeconds }
            val pDurStr = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
            dao.updateProject(proj.copy(scenesCount = updatedScenes.size, duration = pDurStr, updated = "Agora"))
        }
    }

    suspend fun splitScene(projectId: String, sceneId: Long) = withContext(Dispatchers.IO) {
        val scenes = dao.getScenesForProjectOnce(projectId)
        val target = scenes.find { it.id == sceneId } ?: return@withContext
        val midPoint = ((target.trimStartSeconds + target.trimEndSeconds) / 2f)

        // Part 1: startSec .. midPoint
        dao.updateScene(
            target.copy(
                trimEndSeconds = midPoint,
                durationSeconds = (midPoint - target.trimStartSeconds).toInt().coerceAtLeast(1),
                duration = String.format("%02d:%02d", 0, (midPoint - target.trimStartSeconds).toInt().coerceAtLeast(1))
            )
        )

        // Part 2: midPoint .. endSec
        val part2 = target.copy(
            id = 0,
            orderIndex = target.orderIndex + 1,
            label = "${target.label} pt 2",
            trimStartSeconds = midPoint,
            trimEndSeconds = target.trimEndSeconds,
            durationSeconds = (target.trimEndSeconds - midPoint).toInt().coerceAtLeast(1),
            duration = String.format("%02d:%02d", 0, (target.trimEndSeconds - midPoint).toInt().coerceAtLeast(1))
        )

        scenes.filter { it.orderIndex > target.orderIndex }.forEach {
            dao.updateScene(it.copy(orderIndex = it.orderIndex + 1))
        }

        dao.insertScene(part2)

        dao.getProjectByIdOnce(projectId)?.let { proj ->
            val updatedScenes = dao.getScenesForProjectOnce(projectId)
            val totalSeconds = updatedScenes.sumOf { it.durationSeconds }
            val pDurStr = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
            dao.updateProject(proj.copy(scenesCount = updatedScenes.size, duration = pDurStr, updated = "Agora"))
        }
    }

    suspend fun createVoiceClone(name: String, tagline: String, language: String, durationSec: Int): Long = withContext(Dispatchers.IO) {
        dao.insertVoiceClone(
            VoiceCloneEntity(
                name = name,
                tagline = tagline,
                language = language,
                durationSeconds = durationSec,
                status = "ready"
            )
        )
    }

    suspend fun deleteVoiceClone(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteVoiceClone(id)
    }

    suspend fun saveSettings(settings: StudioSettingsEntity) = withContext(Dispatchers.IO) {
        dao.saveSettings(settings)
    }

    suspend fun resetToDemoData() = withContext(Dispatchers.IO) {
        AppDatabase.prepopulateDatabase(dao)
    }
}
