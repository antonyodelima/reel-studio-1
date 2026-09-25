package com.example.reelstudio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reelstudio.data.local.VoiceCloneEntity
import com.example.reelstudio.data.model.ReelDefaults
import com.example.reelstudio.data.model.VoiceInfo
import com.example.reelstudio.data.repository.ReelStudioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VoicesViewModel(private val repository: ReelStudioRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Todos")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _activeVoiceId = MutableStateFlow("sofia")
    val activeVoiceId: StateFlow<String> = _activeVoiceId.asStateFlow()

    private val _playingVoiceId = MutableStateFlow<String?>(null)
    val playingVoiceId: StateFlow<String?> = _playingVoiceId.asStateFlow()

    private val _previewText = MutableStateFlow("Sua ideia merece espaço para chegar com clareza e ritmo.")
    val previewText: StateFlow<String> = _previewText.asStateFlow()

    // Voice Clone Recording State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _recordedAudioSample = MutableStateFlow<String?>(null)
    val recordedAudioSample: StateFlow<String?> = _recordedAudioSample.asStateFlow()

    private val _cloneName = MutableStateFlow("")
    val cloneName: StateFlow<String> = _cloneName.asStateFlow()

    private val _cloneTagline = MutableStateFlow("")
    val cloneTagline: StateFlow<String> = _cloneTagline.asStateFlow()

    private val _cloneLanguage = MutableStateFlow("Português (BR)")
    val cloneLanguage: StateFlow<String> = _cloneLanguage.asStateFlow()

    private val _consentGiven = MutableStateFlow(false)
    val consentGiven: StateFlow<Boolean> = _consentGiven.asStateFlow()

    private var recordingJob: Job? = null
    private var previewJob: Job? = null

    val voiceClones: StateFlow<List<VoiceCloneEntity>> = repository.allVoiceClones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val combinedVoices: StateFlow<List<VoiceInfo>> = combine(
        voiceClones,
        _searchQuery,
        _selectedLanguage
    ) { clones, query, language ->
        val defaultList = ReelDefaults.voices
        val clonedList = clones.map { clone ->
            VoiceInfo(
                id = "clone-${clone.id}",
                name = clone.name,
                role = clone.tagline.ifBlank { "Voz proprietária clonada" },
                language = clone.language,
                colorName = "violet",
                initials = clone.name.take(2).uppercase(),
                isCustomClone = true
            )
        }
        val all = clonedList + defaultList

        all.filter { voice ->
            val matchesQuery = query.isBlank() ||
                voice.name.contains(query, ignoreCase = true) ||
                voice.role.contains(query, ignoreCase = true) ||
                voice.language.contains(query, ignoreCase = true)

            val matchesLang = language == "Todos" || voice.language == language
            matchesQuery && matchesLang
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setActiveVoiceId(id: String) {
        _activeVoiceId.value = id
    }

    fun setPreviewText(text: String) {
        _previewText.value = text
    }

    fun setCloneName(name: String) {
        _cloneName.value = name
    }

    fun setCloneTagline(tag: String) {
        _cloneTagline.value = tag
    }

    fun setCloneLanguage(lang: String) {
        _cloneLanguage.value = lang
    }

    fun setConsentGiven(consent: Boolean) {
        _consentGiven.value = consent
    }

    fun toggleVoicePreview(voiceId: String) {
        if (_playingVoiceId.value == voiceId) {
            _playingVoiceId.value = null
            previewJob?.cancel()
        } else {
            _playingVoiceId.value = voiceId
            previewJob?.cancel()
            previewJob = viewModelScope.launch {
                delay(6000)
                _playingVoiceId.value = null
            }
        }
    }

    fun stopVoicePreview() {
        _playingVoiceId.value = null
        previewJob?.cancel()
    }

    fun startRecording() {
        _isRecording.value = true
        _recordingSeconds.value = 0
        _recordedAudioSample.value = null
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingSeconds.value += 1
                if (_recordingSeconds.value >= 60) {
                    stopRecording()
                }
            }
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        val duration = _recordingSeconds.value.coerceAtLeast(1)
        _recordedAudioSample.value = "gravação-microfone-${duration}s.webm"
    }

    fun clearRecording() {
        _recordedAudioSample.value = null
        _recordingSeconds.value = 0
    }

    fun createClone(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_cloneName.value.isBlank()) {
            onError("Dê um nome para sua voz")
            return
        }
        if (!_consentGiven.value) {
            onError("Confirme que você tem autorização para usar esta voz")
            return
        }
        if (_recordedAudioSample.value == null && _recordingSeconds.value == 0) {
            onError("Grave um áudio de referência pelo microfone")
            return
        }

        viewModelScope.launch {
            repository.createVoiceClone(
                name = _cloneName.value.trim(),
                tagline = _cloneTagline.value.trim(),
                language = _cloneLanguage.value,
                durationSec = _recordingSeconds.value.coerceAtLeast(10)
            )
            _cloneName.value = ""
            _cloneTagline.value = ""
            _recordedAudioSample.value = null
            _recordingSeconds.value = 0
            _consentGiven.value = false
            onSuccess()
        }
    }

    fun deleteClone(id: Long) {
        viewModelScope.launch {
            repository.deleteVoiceClone(id)
        }
    }

    class Factory(private val repository: ReelStudioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VoicesViewModel(repository) as T
        }
    }
}
