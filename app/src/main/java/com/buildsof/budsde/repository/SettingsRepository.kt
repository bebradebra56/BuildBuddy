package com.buildsof.budsde.repository

import android.content.Context
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.data.MeasurementUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val _hasCompletedOnboarding = MutableStateFlow(hasCompletedOnboarding())
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()
    
    private fun loadSettings(): AppSettings {
        return AppSettings(
            units = MeasurementUnit.valueOf(prefs.getString("units", MeasurementUnit.METERS.name) ?: MeasurementUnit.METERS.name),
            currency = Currency.valueOf(prefs.getString("currency", Currency.USD.name) ?: Currency.USD.name),
            defaultMargin = prefs.getInt("default_margin", 10),
            isDarkTheme = prefs.getBoolean("dark_theme", false)
        )
    }
    
    private fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
    }
    
    fun saveSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString("units", settings.units.name)
            putString("currency", settings.currency.name)
            putInt("default_margin", settings.defaultMargin)
            putBoolean("dark_theme", settings.isDarkTheme)
            apply()
        }
        _settings.value = settings
    }
    
    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        _hasCompletedOnboarding.value = true
    }
}
