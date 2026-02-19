package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Dimensions
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EnterDimensionsViewModel(
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
    
    fun updateDimensions(
        dimensions: Dimensions,
        doors: Int = 1,
        windows: Int = 1,
        doorArea: Double = 2.0,
        windowArea: Double = 1.5,
        marginPercent: Int = 10
    ) {
        val currentProject = project ?: return
        val currentRoom = room ?: return
        
        viewModelScope.launch {
            val updatedRoom = currentRoom.copy(
                dimensions = dimensions,
                doors = doors,
                windows = windows,
                doorArea = doorArea,
                windowArea = windowArea,
                marginPercent = marginPercent
            )
            val updatedRooms = currentProject.rooms.map {
                if (it.id == currentRoom.id) updatedRoom else it
            }
            val updatedProject = currentProject.copy(rooms = updatedRooms)
            
            projectRepository.updateProject(updatedProject)
            project = updatedProject
            room = updatedRoom
        }
    }
}
