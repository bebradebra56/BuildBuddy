package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.*
import com.buildsof.budsde.repository.ProjectRepository
import kotlinx.coroutines.launch

class TemplatesViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    var templates by mutableStateOf<List<Template>>(emptyList())
        private set
    
    init {
        initializeTemplates()
    }
    
    private fun initializeTemplates() {
        templates = listOf(
            Template(
                name = "Small Bathroom",
                description = "Complete renovation of a small bathroom",
                roomType = RoomType.BATHROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.WALL_TILES),
                    WorkItem(type = WorkType.PAINT_WALLS),
                    WorkItem(type = WorkType.PRIMER)
                )
            ),
            Template(
                name = "Living Room",
                description = "Standard living room renovation",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS),
                    WorkItem(type = WorkType.LAMINATE),
                    WorkItem(type = WorkType.BASEBOARD)
                )
            ),
            Template(
                name = "Kitchen",
                description = "Modern kitchen renovation",
                roomType = RoomType.KITCHEN,
                workItems = listOf(
                    WorkItem(type = WorkType.WALL_TILES),
                    WorkItem(type = WorkType.PAINT_WALLS),
                    WorkItem(type = WorkType.PRIMER)
                )
            ),
            Template(
                name = "Bedroom",
                description = "Cozy bedroom renovation",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.WALLPAPER),
                    WorkItem(type = WorkType.LAMINATE),
                    WorkItem(type = WorkType.BASEBOARD)
                )
            ),
            Template(
                name = "Office Space",
                description = "Professional office renovation",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS),
                    WorkItem(type = WorkType.LAMINATE),
                    WorkItem(type = WorkType.BASEBOARD)
                )
            ),
            Template(
                name = "Hallway",
                description = "Entrance hallway renovation",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS),
                    WorkItem(type = WorkType.FLOOR_TILES),
                    WorkItem(type = WorkType.BASEBOARD)
                )
            )
        )
    }
    
    fun createProjectFromTemplate(
        template: Template,
        projectName: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val room = Room(
                type = template.roomType,
                name = template.roomType.name,
                dimensions = Dimensions(), // Default dimensions
                workItems = template.workItems
            )
            
            val project = Project(
                name = projectName,
                address = "",
                rooms = listOf(room)
            )
            
            projectRepository.insertProject(project)
            onSuccess(project.id)
        }
    }
}
