package com.diego.playlistmaker.settings.data.impl

import android.util.Log
import com.diego.playlistmaker.settings.data.shared_prefs.SharedTheme
import com.diego.playlistmaker.settings.domain.model.ThemeSettings
import com.diego.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        val isDarkTheme = SharedTheme.getTheme()
        Log.d("SettingsRepository", "getThemeSettings: $isDarkTheme")
        return ThemeSettings(isDarkTheme)
    }

    override fun updateThemeSetting(setting: ThemeSettings) {
        Log.d("SettingsRepository", "updateThemeSetting: ${setting.isDarkTheme}")
        SharedTheme.saveTheme(setting.isDarkTheme)
        SharedTheme.applyTheme()
    }
}