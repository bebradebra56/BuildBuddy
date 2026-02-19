package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreviewViewModel(
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
    
    var room by mutableStateOf<Room?>(null)
        private set
    
    fun loadProjectAndRoom(projectId: String, roomId: String) {
        viewModelScope.launch {
            val loadedProject = projectRepository.getProjectById(projectId)
            project = loadedProject
            room = loadedProject?.rooms?.find { it.id == roomId }
        }
    }
}
