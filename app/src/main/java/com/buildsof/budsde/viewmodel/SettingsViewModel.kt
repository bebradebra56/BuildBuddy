package com.buildsof.budsde.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Project
import com.buildsof.budsde.repository.ProjectRepository
import com.buildsof.budsde.repository.SettingsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
    
    var showExportSuccess by mutableStateOf(false)
    var showImportSuccess by mutableStateOf(false)
    
    fun updateSettings(newSettings: AppSettings) {
        settingsRepository.saveSettings(newSettings)
    }
}
