package com.buildsof.budsde.utils

import android.content.Context
import com.buildsof.budsde.data.Project
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ProjectDataManager(private val context: Context) {
    
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .setPrettyPrinting()
        .create()
    
    private val dataFile = File(context.filesDir, "projects.json")
    
    fun saveProjects(projects: List<Project>): Boolean {
        return try {
            FileWriter(dataFile).use { writer ->
                gson.toJson(projects, writer)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun loadProjects(): List<Project> {
        return try {
            if (!dataFile.exists()) {
                return emptyList()
            }
            FileReader(dataFile).use { reader ->
                val projectsArray = gson.fromJson(reader, Array<Project>::class.java)
                projectsArray?.toList() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun exportProjectToJson(project: Project): String {
        return gson.toJson(project)
    }
    
    fun importProjectFromJson(json: String): Project? {
        return try {
            gson.fromJson(json, Project::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportAllProjectsToJson(projects: List<Project>): String {
        return gson.toJson(projects)
    }
    
    fun importProjectsFromJson(json: String): List<Project>? {
        return try {
            val projectsArray = gson.fromJson(json, Array<Project>::class.java)
            projectsArray?.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
