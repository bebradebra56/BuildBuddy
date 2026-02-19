package com.buildsof.budsde.di

import android.content.Context
import com.buildsof.budsde.data.room.AppDatabase
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository

class AppContainer(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    
    val projectRepository: ProjectRepository by lazy {
        ProjectRepository(database.projectDao())
    }
    
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context.applicationContext)
    }
}
