package com.buildsof.budsde.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.*
import com.buildsof.budsde.data.room.AppDatabase
import com.buildsof.budsde.data.room.toEntity
import com.buildsof.budsde.data.room.toProject
import com.buildsof.budsde.utils.ProjectDataManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val projectDao = database.projectDao()
    private val dataManager = ProjectDataManager(application)
    private val settingsManager = com.buildsof.budsde.utils.SettingsManager(application)
    
    var settings by mutableStateOf(AppSettings())
        private set
    
    var projects by mutableStateOf<List<Project>>(emptyList())
        private set
    
    var hasCompletedOnboarding by mutableStateOf(false)
        private set
    
    // Current editing state
    var currentProject: Project? by mutableStateOf(null)
        private set
    
    var templates by mutableStateOf<List<Template>>(emptyList())
        private set
    
    init {
        // Load saved settings
        loadSettings()
        // Load saved projects from Room
        loadProjects()
        // Initialize with sample templates
        initializeTemplates()
    }
    
    private fun loadSettings() {
        settings = settingsManager.loadSettings()
        hasCompletedOnboarding = settingsManager.hasCompletedOnboarding()
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            try {
                // Observe DB changes continuously
                projectDao.getAllProjects().collect { entities ->
                    projects = entities.map { it.toProject() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                projects = emptyList()
            }
        }
    }
    
    private fun saveProjectToDb(project: Project) {
        viewModelScope.launch {
            try {
                projectDao.insertProject(project.toEntity())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun deleteProjectFromDb(projectId: String) {
        viewModelScope.launch {
            try {
                projectDao.deleteProjectById(projectId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun completeOnboarding() {
        hasCompletedOnboarding = true
        settingsManager.saveOnboardingCompleted(true)
    }
    
    fun createProject(name: String, address: String, startDate: Date, currency: Currency, photoUri: String?) {
        val newProject = Project(
            name = name,
            address = address,
            startDate = startDate,
            currency = currency,
            photoUri = photoUri
        )
        currentProject = newProject
        // Save to DB - collect() will update projects automatically
        saveProjectToDb(newProject)
    }
    
    fun updateProject(projectId: String, updater: (Project) -> Project) {
        val project = projects.find { it.id == projectId } ?: return
        val updatedProject = updater(project)
        
        if (currentProject?.id == projectId) {
            currentProject = updatedProject
        }
        
        // Save to DB - collect() will update projects automatically
        saveProjectToDb(updatedProject)
    }
    
    fun deleteProject(projectId: String) {
        if (currentProject?.id == projectId) {
            currentProject = null
        }
        
        // Delete from DB - collect() will update projects automatically
        deleteProjectFromDb(projectId)
    }
    
    fun exportAllProjects(): String {
        return dataManager.exportAllProjectsToJson(projects)
    }
    
    fun importProjects(json: String): Boolean {
        val importedProjects = dataManager.importProjectsFromJson(json)
        return if (importedProjects != null) {
            viewModelScope.launch {
                importedProjects.forEach { project ->
                    projectDao.insertProject(project.toEntity())
                }
            }
            true
        } else {
            false
        }
    }
    
    fun getProject(projectId: String): Project? {
        return projects.find { it.id == projectId }
    }
    
    fun addRoomToProject(projectId: String, room: Room) {
        updateProject(projectId) { project ->
            project.copy(rooms = project.rooms + room)
        }
    }
    
    fun updateRoom(projectId: String, roomId: String, updater: (Room) -> Room) {
        updateProject(projectId) { project ->
            project.copy(
                rooms = project.rooms.map { if (it.id == roomId) updater(it) else it }
            )
        }
    }
    
    fun getRoom(projectId: String, roomId: String): Room? {
        return getProject(projectId)?.rooms?.find { it.id == roomId }
    }
    
    fun updateWorkItem(projectId: String, roomId: String, workItemId: String, updater: (WorkItem) -> WorkItem) {
        updateRoom(projectId, roomId) { room ->
            room.copy(
                workItems = room.workItems.map { if (it.id == workItemId) updater(it) else it }
            )
        }
    }
    
    fun addWorkItem(projectId: String, roomId: String, workItem: WorkItem) {
        updateRoom(projectId, roomId) { room ->
            room.copy(workItems = room.workItems + workItem)
        }
    }
    
    fun toggleWorkItem(projectId: String, roomId: String, workItemId: String, enabled: Boolean) {
        updateWorkItem(projectId, roomId, workItemId) { workItem ->
            workItem.copy(enabled = enabled)
        }
    }
    
    fun getShoppingList(projectId: String): List<ShoppingItem> {
        val project = getProject(projectId) ?: return emptyList()
        val items = mutableListOf<ShoppingItem>()
        
        project.rooms.forEach { room ->
            room.workItems.filter { it.enabled }.forEach { workItem ->
                items.addAll(calculateMaterialsForWorkItem(room, workItem))
            }
        }
        
        return items
    }
    
    private fun calculateMaterialsForWorkItem(room: Room, workItem: WorkItem): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        val dims = room.dimensions
        val marginMultiplier = 1 + (room.marginPercent / 100.0)
        
        when (workItem.type) {
            WorkType.PAINT_WALLS -> {
                val config = workItem.config as? WorkConfig.PaintConfig ?: return emptyList()
                val wallArea = dims.wallArea - (room.doors * room.doorArea) - (room.windows * room.windowArea)
                val adjustedArea = wallArea * marginMultiplier
                val litersNeeded = (adjustedArea / config.coverage) * config.layers
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "${config.brand} Paint (${config.paintType})",
                    quantity = litersNeeded,
                    unit = "L",
                    price = litersNeeded * config.pricePerLiter
                ))
                
                if (config.includeRoller) {
                    items.add(ShoppingItem(
                        category = ShoppingCategory.TOOLS,
                        name = "Paint Roller",
                        quantity = 1.0,
                        unit = "pcs",
                        price = 8.0
                    ))
                }
                if (config.includeBrush) {
                    items.add(ShoppingItem(
                        category = ShoppingCategory.TOOLS,
                        name = "Paint Brush",
                        quantity = 1.0,
                        unit = "pcs",
                        price = 5.0
                    ))
                }
                if (config.includeTray) {
                    items.add(ShoppingItem(
                        category = ShoppingCategory.TOOLS,
                        name = "Paint Tray",
                        quantity = 1.0,
                        unit = "pcs",
                        price = 4.0
                    ))
                }
            }
            
            WorkType.WALLPAPER -> {
                val config = workItem.config as? WorkConfig.WallpaperConfig ?: return emptyList()
                val wallArea = dims.wallArea - (room.doors * room.doorArea) - (room.windows * room.windowArea)
                val adjustedArea = wallArea * marginMultiplier
                val rollArea = config.rollWidth * config.rollLength
                val rollsNeeded = kotlin.math.ceil(adjustedArea / rollArea).toInt()
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "${config.type} Wallpaper",
                    quantity = rollsNeeded.toDouble(),
                    unit = "rolls",
                    price = rollsNeeded * config.pricePerRoll
                ))
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "Wallpaper Glue (${config.glueType})",
                    quantity = kotlin.math.ceil(adjustedArea / 30).toDouble(),
                    unit = "packs",
                    price = kotlin.math.ceil(adjustedArea / 30) * 10.0
                ))
            }
            
            WorkType.FLOOR_TILES, WorkType.WALL_TILES -> {
                val config = workItem.config as? WorkConfig.TileConfig ?: return emptyList()
                val area = if (workItem.type == WorkType.FLOOR_TILES) dims.floorArea else dims.wallArea
                val adjustedArea = area * (1 + config.margin / 100.0)
                val tileArea = (config.tileWidth / 100.0) * (config.tileHeight / 100.0)
                val tilesNeeded = kotlin.math.ceil(adjustedArea / tileArea).toInt()
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "Tiles ${config.tileWidth}x${config.tileHeight}cm",
                    quantity = adjustedArea,
                    unit = "m²",
                    price = adjustedArea * config.pricePerM2
                ))
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "Tile Adhesive",
                    quantity = adjustedArea * config.glue.coverageKgPerM2,
                    unit = "kg",
                    price = adjustedArea * config.glue.coverageKgPerM2 * config.glue.pricePerKg
                ))
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "Tile Grout",
                    quantity = adjustedArea * 0.5,
                    unit = "kg",
                    price = adjustedArea * 0.5 * config.grout.pricePerKg
                ))
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.CONSUMABLES,
                    name = "Tile Spacers ${config.spacerSize}mm",
                    quantity = (tilesNeeded * 4.0) / 100.0,
                    unit = "packs",
                    price = kotlin.math.ceil((tilesNeeded * 4.0) / 100.0) * 3.0
                ))
            }
            
            WorkType.LAMINATE -> {
                val config = workItem.config as? WorkConfig.LaminateConfig ?: return emptyList()
                val adjustedArea = dims.floorArea * marginMultiplier
                
                items.add(ShoppingItem(
                    category = ShoppingCategory.MATERIALS,
                    name = "${config.type} Flooring (Class ${config.classRating})",
                    quantity = adjustedArea,
                    unit = "m²",
                    price = adjustedArea * config.pricePerM2
                ))
                
                config.underlayment?.let { underlay ->
                    items.add(ShoppingItem(
                        category = ShoppingCategory.MATERIALS,
                        name = "Underlayment ${underlay.thickness}mm",
                        quantity = adjustedArea,
                        unit = "m²",
                        price = adjustedArea * underlay.pricePerM2
                    ))
                }
                
                if (config.includeBaseboard) {
                    val perimeter = 2 * (dims.length + dims.width)
                    items.add(ShoppingItem(
                        category = ShoppingCategory.MATERIALS,
                        name = "Baseboard",
                        quantity = perimeter,
                        unit = "m",
                        price = perimeter * 5.0
                    ))
                }
            }
            
            else -> {}
        }
        
        return items
    }
    
    fun getTotalBudget(projectId: String): Double {
        return getShoppingList(projectId).sumOf { it.price }
    }
    
    fun updateShoppingItemPurchased(projectId: String, itemId: String, isPurchased: Boolean) {
        // In a real app, we'd persist this separately
    }
    
    fun addNote(projectId: String, note: Note) {
        updateProject(projectId) { project ->
            project.copy(notes = project.notes + note)
        }
    }
    
    fun addTaskToNote(projectId: String, noteId: String, task: Task) {
        updateProject(projectId) { project ->
            project.copy(
                notes = project.notes.map { note ->
                    if (note.id == noteId) {
                        note.copy(tasks = note.tasks + task)
                    } else note
                }
            )
        }
    }
    
    fun toggleTask(projectId: String, noteId: String, taskId: String) {
        updateProject(projectId) { project ->
            project.copy(
                notes = project.notes.map { note ->
                    if (note.id == noteId) {
                        note.copy(
                            tasks = note.tasks.map { task ->
                                if (task.id == taskId) {
                                    task.copy(isCompleted = !task.isCompleted)
                                } else task
                            }
                        )
                    } else note
                }
            )
        }
    }
    
    fun addPhotoToNote(projectId: String, noteId: String, photoUri: String) {
        updateProject(projectId) { project ->
            project.copy(
                notes = project.notes.map { note ->
                    if (note.id == noteId) {
                        note.copy(photos = note.photos + photoUri)
                    } else note
                }
            )
        }
    }
    
    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
        settingsManager.saveSettings(newSettings)
    }
    
    private fun initializeTemplates() {
        templates = listOf(
            Template(
                name = "Bathroom Basic",
                description = "Essential bathroom renovation with tiles and primer",
                roomType = RoomType.BATHROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.WALL_TILES, enabled = true),
                    WorkItem(type = WorkType.FLOOR_TILES, enabled = true),
                    WorkItem(type = WorkType.PRIMER, enabled = true)
                )
            ),
            Template(
                name = "Kitchen Light",
                description = "Simple kitchen refresh with paint and flooring",
                roomType = RoomType.KITCHEN,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS, enabled = true),
                    WorkItem(type = WorkType.LAMINATE, enabled = true),
                    WorkItem(type = WorkType.BASEBOARD, enabled = true)
                )
            ),
            Template(
                name = "Living Room Paint",
                description = "Complete room painting with primer",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS, enabled = true),
                    WorkItem(type = WorkType.CEILING_PAINT, enabled = true),
                    WorkItem(type = WorkType.PRIMER, enabled = true),
                    WorkItem(type = WorkType.BASEBOARD, enabled = true)
                )
            ),
            Template(
                name = "Bedroom Wallpaper",
                description = "Elegant bedroom with wallpaper",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.WALLPAPER, enabled = true),
                    WorkItem(type = WorkType.CEILING_PAINT, enabled = true),
                    WorkItem(type = WorkType.LAMINATE, enabled = true),
                    WorkItem(type = WorkType.BASEBOARD, enabled = true)
                )
            ),
            Template(
                name = "Balcony Floor & Walls",
                description = "Complete balcony renovation with tiles",
                roomType = RoomType.BALCONY,
                workItems = listOf(
                    WorkItem(type = WorkType.FLOOR_TILES, enabled = true),
                    WorkItem(type = WorkType.WALL_TILES, enabled = true),
                    WorkItem(type = WorkType.PUTTY, enabled = true)
                )
            ),
            Template(
                name = "Office Modern",
                description = "Modern office space with laminate",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS, enabled = true),
                    WorkItem(type = WorkType.LAMINATE, enabled = true),
                    WorkItem(type = WorkType.BASEBOARD, enabled = true),
                    WorkItem(type = WorkType.PRIMER, enabled = true)
                )
            ),
            Template(
                name = "Hallway Quick",
                description = "Quick hallway makeover",
                roomType = RoomType.HALLWAY,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS, enabled = true),
                    WorkItem(type = WorkType.LAMINATE, enabled = true)
                )
            ),
            Template(
                name = "Kids Room Fun",
                description = "Colorful and safe kids room",
                roomType = RoomType.ROOM,
                workItems = listOf(
                    WorkItem(type = WorkType.PAINT_WALLS, enabled = true),
                    WorkItem(type = WorkType.LAMINATE, enabled = true),
                    WorkItem(type = WorkType.BASEBOARD, enabled = true),
                    WorkItem(type = WorkType.PUTTY, enabled = true)
                )
            )
        )
    }
    
    fun applyTemplate(projectId: String, template: Template, roomName: String, dimensions: Dimensions) {
        val room = Room(
            type = template.roomType,
            name = roomName,
            dimensions = dimensions,
            workItems = template.workItems.map { it.copy() }
        )
        addRoomToProject(projectId, room)
    }
}
