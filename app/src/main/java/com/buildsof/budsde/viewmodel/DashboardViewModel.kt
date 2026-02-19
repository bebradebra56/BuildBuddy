package com.buildsof.budsde.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    projectRepository: ProjectRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    
    val projects: StateFlow<List<Project>> = projectRepository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
}
