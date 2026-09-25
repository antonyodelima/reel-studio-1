package com.example.reelstudio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reelstudio.data.local.ProjectEntity
import com.example.reelstudio.data.model.ProjectStatus
import com.example.reelstudio.data.repository.ReelStudioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ReelStudioRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos os projetos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProjects: StateFlow<List<ProjectEntity>> = combine(
        repository.allProjects,
        _searchQuery,
        _selectedFilter
    ) { projects, query, filter ->
        projects.filter { proj ->
            val matchesQuery = query.isBlank() ||
                proj.name.contains(query, ignoreCase = true) ||
                proj.preset.contains(query, ignoreCase = true)

            val statusLabel = ProjectStatus.fromString(proj.status).labelPt
            val matchesFilter = when (filter) {
                "Todos os projetos" -> true
                "Draft", "Rascunho" -> proj.status.equals("draft", ignoreCase = true)
                "Rendering", "Renderizando" -> proj.status.equals("rendering", ignoreCase = true)
                "Ready", "Pronto" -> proj.status.equals("ready", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun createProject(name: String, preset: String, brief: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val project = repository.createProject(name, preset, brief)
            onCreated(project.id)
        }
    }

    fun startRender(projectId: String) {
        viewModelScope.launch {
            repository.updateProjectStatus(projectId, "rendering")
            // Simulate video rendering completion after 4 seconds
            delay(4000)
            repository.updateProjectStatus(projectId, "ready")
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    fun duplicateProject(projectId: String, onDuplicated: (String) -> Unit = {}) {
        viewModelScope.launch {
            val dup = repository.duplicateProject(projectId)
            dup?.let { onDuplicated(it.id) }
        }
    }

    suspend fun getScenes(projectId: String) = repository.getScenesSync(projectId)

    class Factory(private val repository: ReelStudioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
