package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.Dimensions
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.data.RoomType
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch

class SelectZoneViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    var project by mutableStateOf<Project?>(null)
        private set
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            project = projectRepository.getProjectById(projectId)
        }
    }
    
    fun addRoom(type: RoomType, name: String) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            val newRoom = Room(
                type = type,
                name = name,
                dimensions = Dimensions() // Default dimensions will be set later
            )
            val updatedProject = currentProject.copy(
                rooms = currentProject.rooms + newRoom
            )
            projectRepository.updateProject(updatedProject)
            project = updatedProject
        }
    }
}
