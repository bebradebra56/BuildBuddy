package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.Note
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch
import java.util.*

class NotesViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    var project by mutableStateOf<Project?>(null)
        private set
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            project = projectRepository.getProjectById(projectId)
        }
    }
    
    fun addNote(text: String, photoUri: String?) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            val note = Note(
                text = text,
                photos = if (photoUri != null) listOf(photoUri) else emptyList(),
                createdAt = Date()
            )
            val updatedProject = currentProject.copy(
                notes = currentProject.notes + note
            )
            projectRepository.updateProject(updatedProject)
            project = updatedProject
        }
    }
    
    fun deleteNote(noteId: String) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            val updatedProject = currentProject.copy(
                notes = currentProject.notes.filter { it.id != noteId }
            )
            projectRepository.updateProject(updatedProject)
            project = updatedProject
        }
    }
    
    fun addPhotoToNote(noteId: String, photoUri: String) {
        val currentProject = project ?: return
        
        viewModelScope.launch {
            val updatedNotes = currentProject.notes.map { note ->
                if (note.id == noteId) {
                    note.copy(photos = note.photos + photoUri)
                } else {
                    note
                }
            }
            val updatedProject = currentProject.copy(notes = updatedNotes)
            projectRepository.updateProject(updatedProject)
            project = updatedProject
        }
    }
}
