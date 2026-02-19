package com.buildsof.budsde.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsof.budsde.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SplashViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {
    
    val hasCompletedOnboarding: StateFlow<Boolean> = settingsRepository.hasCompletedOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
