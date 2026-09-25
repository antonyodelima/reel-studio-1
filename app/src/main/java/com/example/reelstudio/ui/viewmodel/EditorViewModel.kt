package com.example.reelstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reelstudio.data.local.ProjectEntity
import com.example.reelstudio.data.local.SceneEntity
import com.example.reelstudio.data.local.VoiceCloneEntity
import com.example.reelstudio.data.repository.ReelStudioRepository
import com.example.reelstudio.util.ExportResult
import com.example.reelstudio.util.Mp4Exporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: ReelStudioRepository,
    private val projectId: String
) : ViewModel() {

    val project: StateFlow<ProjectEntity?> = repository.getProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val scenes: StateFlow<List<SceneEntity>> = repository.getScenes(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceClones: StateFlow<List<VoiceCloneEntity>> = repository.allVoiceClones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSceneIndex = MutableStateFlow(0)
    val activeSceneIndex: StateFlow<Int> = _activeSceneIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedTab = MutableStateFlow("Cenas")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _selectedVoice = MutableStateFlow("Sofia")
    val selectedVoice: StateFlow<String> = _selectedVoice.asStateFlow()

    private val _selectedSoundtrack = MutableStateFlow("Brilho ambiente")
    val selectedSoundtrack: StateFlow<String> = _selectedSoundtrack.asStateFlow()

    private val _musicVolume = MutableStateFlow(24f)
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()

    private val _isGeneratingVoice = MutableStateFlow(false)
    val isGeneratingVoice: StateFlow<Boolean> = _isGeneratingVoice.asStateFlow()

    private val _renderProgress = MutableStateFlow<Int?>(null)
    val renderProgress: StateFlow<Int?> = _renderProgress.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private var playbackJob: Job? = null

    fun selectScene(index: Int) {
        val count = scenes.value.size
        if (count > 0) {
            _activeSceneIndex.value = index.coerceIn(0, count - 1)
        }
    }

    fun setTab(tab: String) {
        _selectedTab.value = tab
    }

    fun setVoice(voice: String) {
        _selectedVoice.value = voice
    }

    fun setSoundtrack(track: String) {
        _selectedSoundtrack.value = track
    }

    fun setMusicVolume(vol: Float) {
        _musicVolume.value = vol
    }

    fun togglePlay() {
        if (_isPlaying.value) {
            pausePreview()
        } else {
            playPreview()
        }
    }

    private fun playPreview() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val currentScenes = scenes.value
                if (currentScenes.isEmpty()) break
                val scene = currentScenes.getOrNull(_activeSceneIndex.value)
                val durationMs = ((scene?.durationSeconds ?: 4) * 1000L).coerceAtLeast(2000L)
                delay(durationMs)
                if (_isPlaying.value) {
                    val next = (_activeSceneIndex.value + 1) % currentScenes.size
                    _activeSceneIndex.value = next
                }
            }
        }
    }

    private fun pausePreview() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun updateProjectName(name: String) {
        viewModelScope.launch {
            project.value?.let { proj ->
                repository.updateProject(proj.copy(name = name, updated = "Agora"))
            }
        }
    }

    fun updateSceneTitle(title: String) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateScene(scene.copy(title = title, caption = title))
            }
        }
    }

    fun updateSceneCaption(caption: String) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateSceneCaption(scene.id, projectId, caption)
            }
        }
    }

    fun updateSceneDuration(seconds: Int) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateSceneDuration(scene.id, projectId, seconds)
            }
        }
    }

    fun saveProjectNow(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            project.value?.let { proj ->
                repository.updateProject(proj.copy(updated = "Agora"))
            }
            onSaved()
        }
    }

    fun updateSceneMedia(mediaType: String) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateScene(scene.copy(mediaType = mediaType))
            }
        }
    }

    fun updateSceneCaptionStyle(style: String) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateScene(scene.copy(captionStyle = style))
            }
        }
    }

    fun updateSceneTransition(transition: String) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateScene(scene.copy(transition = transition))
            }
        }
    }

    fun toggleSceneLock() {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.updateScene(scene.copy(isLocked = !scene.isLocked))
            }
        }
    }

    fun regenerateScene() {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            val alternates = listOf(
                "O jeito antigo de produzir conteúdo acabou.",
                "Transforme ideias complexas em segundos visuais.",
                "Seu próximo reel com retenção máxima.",
                "Produção contínua que acompanha sua velocidade."
            )
            val nextTitle = alternates[(index + alternates.size) % alternates.size]
            viewModelScope.launch {
                repository.updateScene(scene.copy(title = nextTitle, caption = nextTitle))
            }
        }
    }

    fun addScene() {
        viewModelScope.launch {
            val currentScenes = scenes.value
            val newScene = repository.addScene(projectId, currentScenes.size)
            _activeSceneIndex.value = currentScenes.size
        }
    }

    fun deleteScene(sceneId: Long) {
        viewModelScope.launch {
            repository.deleteScene(sceneId, projectId)
            val currentScenes = scenes.value
            if (_activeSceneIndex.value >= currentScenes.size - 1) {
                _activeSceneIndex.value = (currentScenes.size - 2).coerceAtLeast(0)
            }
        }
    }

    fun generateVoiceForCurrentScene(onFinished: (voiceName: String, text: String) -> Unit = { _, _ -> }) {
        val currentScenes = scenes.value
        val index = _activeSceneIndex.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                _isGeneratingVoice.value = true
                repository.updateScene(scene.copy(voiceStatus = "generating", voiceName = _selectedVoice.value))
                delay(1200)
                repository.updateScene(scene.copy(voiceStatus = "ready", voiceName = _selectedVoice.value))
                _isGeneratingVoice.value = false
                onFinished(_selectedVoice.value, scene.caption.ifBlank { scene.title })
            }
        }
    }

    fun moveClipLeft(index: Int) {
        if (index > 0) {
            viewModelScope.launch {
                repository.reorderScenes(projectId, index, index - 1)
                _activeSceneIndex.value = index - 1
            }
        }
    }

    fun moveClipRight(index: Int) {
        val currentScenes = scenes.value
        if (index < currentScenes.size - 1) {
            viewModelScope.launch {
                repository.reorderScenes(projectId, index, index + 1)
                _activeSceneIndex.value = index + 1
            }
        }
    }

    fun trimClip(sceneId: Long, startSec: Float, endSec: Float) {
        viewModelScope.launch {
            repository.trimScene(sceneId, projectId, startSec, endSec)
        }
    }

    fun duplicateClip(index: Int) {
        val currentScenes = scenes.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.duplicateScene(projectId, scene.id)
            }
        }
    }

    fun splitClip(index: Int) {
        val currentScenes = scenes.value
        if (index in currentScenes.indices) {
            val scene = currentScenes[index]
            viewModelScope.launch {
                repository.splitScene(projectId, scene.id)
            }
        }
    }

    fun exportSingleMp4(
        context: Context,
        format: String = "9:16 Portrait",
        quality: String = "1080p",
        engine: String = "Remotion",
        onFinished: (ExportResult) -> Unit
    ) {
        val currentProject = project.value ?: return
        val currentScenes = scenes.value
        if (currentScenes.isEmpty()) return

        viewModelScope.launch {
            _isExporting.value = true
            _renderProgress.value = 5

            val result = Mp4Exporter.exportProjectToMp4(
                context = context,
                project = currentProject,
                scenes = currentScenes,
                format = format,
                quality = quality,
                engine = engine,
                onProgress = { progress ->
                    _renderProgress.value = progress
                }
            )

            repository.saveExportedMp4(
                projectId = projectId,
                uri = result.contentUri.toString(),
                sizeFormatted = result.sizeFormatted
            )

            _exportResult.value = result
            _isExporting.value = false
            onFinished(result)
        }
    }

    fun shareExportedMp4(context: Context) {
        _exportResult.value?.let { result ->
            Mp4Exporter.shareMp4(context, result.file, project.value?.name ?: "Reel Studio Export")
        }
    }

    fun saveExportedMp4ToGallery(context: Context): Boolean {
        val result = _exportResult.value ?: return false
        return Mp4Exporter.saveToGallery(context, result.file, project.value?.name ?: "Reel Studio Video")
    }

    fun startProductionRender(format: String, quality: String, engine: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            _renderProgress.value = 5
            for (p in 10..100 step 15) {
                delay(400)
                _renderProgress.value = p
            }
            repository.updateProjectStatus(projectId, "ready")
            delay(500)
            _renderProgress.value = null
            onFinished()
        }
    }

    fun dismissRenderDialog() {
        _renderProgress.value = null
    }

    fun dismissExportResult() {
        _exportResult.value = null
    }

    class Factory(
        private val repository: ReelStudioRepository,
        private val projectId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(repository, projectId) as T
        }
    }
}
