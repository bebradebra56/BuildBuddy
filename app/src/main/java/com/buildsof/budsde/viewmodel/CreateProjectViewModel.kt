package com.buildsof.budsde.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class CreateProjectViewModel(
    private val projectRepository: ProjectRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
    
    var createdProjectId by mutableStateOf<String?>(null)
        private set
    
    fun createProject(
        name: String,
        address: String,
        startDate: Date,
        photoUri: String?,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val project = Project(
                name = name,
                address = address,
                startDate = startDate,
                currency = settings.value.currency,
                photoUri = photoUri
            )
            projectRepository.insertProject(project)
            createdProjectId = project.id
            onSuccess(project.id)
        }
    }
}
