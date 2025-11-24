package com.diego.playlistmaker.domain.repositories

interface SettingsThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(isDarkTheme: Boolean)
    fun applyTheme()
}