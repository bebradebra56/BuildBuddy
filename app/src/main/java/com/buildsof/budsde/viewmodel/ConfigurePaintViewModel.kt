package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.*
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch

class ConfigurePaintViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    var project by mutableStateOf<Project?>(null)
        private set
    
    var room by mutableStateOf<Room?>(null)
        private set
    
    var workItem by mutableStateOf<WorkItem?>(null)
        private set
    
    fun loadData(projectId: String, roomId: String, workItemId: String) {
        viewModelScope.launch {
            val loadedProject = projectRepository.getProjectById(projectId)
            project = loadedProject
            
            val loadedRoom = loadedProject?.rooms?.find { it.id == roomId }
            room = loadedRoom
            
            workItem = loadedRoom?.workItems?.find { it.id == workItemId }
        }
    }
    
    fun savePaintConfig(config: WorkConfig.PaintConfig) {
        val currentProject = project ?: return
        val currentRoom = room ?: return
        val currentWorkItem = workItem ?: return
        
        viewModelScope.launch {
            val updatedWorkItem = currentWorkItem.copy(
                config = config,
                enabled = true  // Mark as enabled when configured
            )
            val updatedWorkItems = currentRoom.workItems.map {
                if (it.id == currentWorkItem.id) updatedWorkItem else it
            }
            val updatedRoom = currentRoom.copy(workItems = updatedWorkItems)
            val updatedRooms = currentProject.rooms.map {
                if (it.id == currentRoom.id) updatedRoom else it
            }
            val updatedProject = currentProject.copy(rooms = updatedRooms)
            
            projectRepository.updateProject(updatedProject)
            project = updatedProject
            room = updatedRoom
            workItem = updatedWorkItem
        }
    }
}
