package com.diego.playlistmaker.settings.domain.repository

import com.diego.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(setting: ThemeSettings)
}