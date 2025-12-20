package com.diego.playlistmaker.settings.domain.impl

import com.diego.playlistmaker.settings.domain.model.ThemeSettings
import com.diego.playlistmaker.settings.domain.repository.SettingsInteractor
import com.diego.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(setting: ThemeSettings) {
        return repository.updateThemeSetting(setting)
    }
}