package com.buildsof.budsde.utils

import android.content.Context
import android.content.SharedPreferences
import com.buildsof.budsde.data.AppSettings
import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.data.MeasurementUnit

class SettingsManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_settings",
        Context.MODE_PRIVATE
    )
    
    fun saveSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString("units", settings.units.name)
            putString("currency", settings.currency.name)
            putInt("default_margin", settings.defaultMargin)
            putBoolean("is_dark_theme", settings.isDarkTheme)
            apply()
        }
    }
    
    fun loadSettings(): AppSettings {
        return AppSettings(
            units = try {
                MeasurementUnit.valueOf(prefs.getString("units", MeasurementUnit.METERS.name)!!)
            } catch (e: Exception) {
                MeasurementUnit.METERS
            },
            currency = try {
                Currency.valueOf(prefs.getString("currency", Currency.USD.name)!!)
            } catch (e: Exception) {
                Currency.USD
            },
            defaultMargin = prefs.getInt("default_margin", 10),
            isDarkTheme = prefs.getBoolean("is_dark_theme", false)
        )
    }
    
    fun saveOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }
    
    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
    }
}
