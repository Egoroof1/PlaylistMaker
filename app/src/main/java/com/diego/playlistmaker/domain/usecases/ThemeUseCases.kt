package com.diego.playlistmaker.domain.usecases

import com.diego.playlistmaker.domain.repositories.SettingsThemeRepository

class GetThemeUseCase(private val settingsRepository: SettingsThemeRepository) {
    fun execute(): Boolean = settingsRepository.getTheme()
}

class SaveThemeUseCase(private val settingsRepository: SettingsThemeRepository) {
    fun execute(isDarkTheme: Boolean) = settingsRepository.saveTheme(isDarkTheme)
}

class ApplyThemeUseCase(private val settingsRepository: SettingsThemeRepository) {
    fun execute() = settingsRepository.applyTheme()
}