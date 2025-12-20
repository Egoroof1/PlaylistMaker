package com.diego.playlistmaker.settings.domain.repository

import com.diego.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(setting: ThemeSettings)
}

/**
 * Его задача — сохранить/получить данные, как и у SettingsRepository:
 * Выходит, что SettingsInteractor только передаёт вызовы SettingsRepository —
 * делегирует ему задачу, а особой логики не имеет.
 */