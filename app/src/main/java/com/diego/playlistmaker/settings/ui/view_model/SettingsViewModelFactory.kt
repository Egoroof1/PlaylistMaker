package com.diego.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.playlistmaker.settings.domain.repository.SettingsInteractor
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor

class SettingsViewModelFactory(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(sharingInteractor, settingsInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}