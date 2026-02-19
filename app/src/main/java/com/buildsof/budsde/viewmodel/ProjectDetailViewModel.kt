package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
    
    var project by mutableStateOf<Project?>(null)
        private set
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            project = projectRepository.getProjectById(projectId)
        }
    }
    
    fun updateProject(name: String, address: String) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            val updatedProject = currentProject.copy(
                name = name,
                address = address
            )
            projectRepository.updateProject(updatedProject)
            project = updatedProject
        }
    }
    
    fun deleteProject(onDeleted: () -> Unit) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            projectRepository.deleteProject(currentProject.id)
            onDeleted()
        }
    }
}
