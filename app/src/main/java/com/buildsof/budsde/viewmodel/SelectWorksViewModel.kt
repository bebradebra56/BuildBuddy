package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.data.WorkItem
import com.buildsof.budsde.data.WorkType
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch

class SelectWorksViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
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
    
    fun toggleWorkItem(workType: WorkType, enabled: Boolean) {
        val currentProject = project ?: return
        val currentRoom = room ?: return
        
        viewModelScope.launch {
            val updatedWorkItems = if (enabled) {
                // Add new work item if doesn't exist
                if (currentRoom.workItems.any { it.type == workType }) {
                    currentRoom.workItems
                } else {
                    currentRoom.workItems + WorkItem(type = workType)
                }
            } else {
                // Remove work item
                currentRoom.workItems.filter { it.type != workType }
            }
            
            val updatedRoom = currentRoom.copy(workItems = updatedWorkItems)
            val updatedRooms = currentProject.rooms.map {
                if (it.id == currentRoom.id) updatedRoom else it
            }
            val updatedProject = currentProject.copy(rooms = updatedRooms)
            
            projectRepository.updateProject(updatedProject)
            project = updatedProject
            room = updatedRoom
        }
    }
    
    fun getWorkItem(workType: WorkType): WorkItem? {
        return room?.workItems?.find { it.type == workType }
    }
}
