package com.example.reelstudio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reelstudio.data.local.StudioSettingsEntity
import com.example.reelstudio.data.repository.ReelStudioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: ReelStudioRepository) : ViewModel() {

    val settings: StateFlow<StudioSettingsEntity?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateWorkspaceName(name: String) {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(workspaceName = name))
        }
    }

    fun updateDefaultLanguage(lang: String) {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(defaultLanguage = lang))
        }
    }

    fun updateDefaultFormat(format: String) {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(defaultFormat = format))
        }
    }

    fun updateEngine(engine: String) {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(engine = engine))
        }
    }

    fun toggleAutosave() {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(autosave = !current.autosave))
        }
    }

    fun toggleCaptions() {
        val current = settings.value ?: StudioSettingsEntity()
        viewModelScope.launch {
            repository.saveSettings(current.copy(captions = !current.captions))
        }
    }

    fun resetToDemo() {
        viewModelScope.launch {
            repository.resetToDemoData()
        }
    }

    class Factory(private val repository: ReelStudioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
