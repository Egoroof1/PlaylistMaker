package com.diego.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diego.playlistmaker.settings.domain.model.ThemeSettings
import com.diego.playlistmaker.settings.domain.repository.SettingsInteractor
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> = _themeSettings

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        val settings = settingsInteractor.getThemeSettings()
        _themeSettings.value = settings
    }

    fun updateTheme(isDarkTheme: Boolean) {
        val settings = ThemeSettings(isDarkTheme)
        settingsInteractor.updateThemeSetting(settings)
        _themeSettings.value = settings
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun contactSupport() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openTerms()
    }
}