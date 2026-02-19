package com.buildsof.budsde.viewmodel

import androidx.lifecycle.ViewModel
import com.buildsof.budsde.repository.SettingsRepository

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    fun completeOnboarding() {
        settingsRepository.completeOnboarding()
    }
}
