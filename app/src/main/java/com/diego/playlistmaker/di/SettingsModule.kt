package com.diego.playlistmaker.di

import com.diego.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.diego.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.diego.playlistmaker.settings.domain.repository.SettingsInteractor
import com.diego.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.dsl.module

val settingsModule = module {
    // Settings
    single<SettingsRepository> {
        SettingsRepositoryImpl()
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}