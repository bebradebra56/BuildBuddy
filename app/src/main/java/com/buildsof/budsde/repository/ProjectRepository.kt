package com.buildsof.budsde.repository

import com.buildsof.budsde.data.*
import com.buildsof.budsde.data.room.ProjectDao
import com.buildsof.budsde.data.room.toEntity
import com.buildsof.budsde.data.room.toProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class ProjectRepository(private val projectDao: ProjectDao) {
    
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects().map { entities ->
        entities.map { it.toProject() }
    }
    
    suspend fun getProjectById(projectId: String): Project? {
        return projectDao.getProjectById(projectId)?.toProject()
    }
    
    suspend fun insertProject(project: Project) {
        projectDao.insertProject(project.toEntity())
    }
    
    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.toEntity())
    }
    
    suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
    }
    
    suspend fun deleteAllProjects() {
        projectDao.deleteAllProjects()
    }
    
    suspend fun insertProjects(projects: List<Project>) {
        projectDao.insertProjects(projects.map { it.toEntity() })
    }
}
