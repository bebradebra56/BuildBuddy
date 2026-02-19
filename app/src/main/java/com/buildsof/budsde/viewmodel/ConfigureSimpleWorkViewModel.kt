package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.data.WorkItem
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch

class ConfigureSimpleWorkViewModel(
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
    
    fun saveSimpleConfig() {
        val currentProject = project ?: return
        val currentRoom = room ?: return
        val currentWorkItem = workItem ?: return
        
        viewModelScope.launch {
            // Mark the work item as enabled (configured)
            val updatedWorkItems = currentRoom.workItems.map { item ->
                if (item.id == currentWorkItem.id) {
                    item.copy(enabled = true)
                } else {
                    item
                }
            }
            
            val updatedRoom = currentRoom.copy(workItems = updatedWorkItems)
            
            val updatedRooms = currentProject.rooms.map { r ->
                if (r.id == currentRoom.id) updatedRoom else r
            }
            
            val updatedProject = currentProject.copy(rooms = updatedRooms)
            projectRepository.updateProject(updatedProject)
        }
    }
}
