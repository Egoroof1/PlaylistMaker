package com.diego.playlistmaker.config

import android.app.Application
import com.diego.playlistmaker.di.dataModule
import com.diego.playlistmaker.di.repositoryModule
import com.diego.playlistmaker.di.settingsModule
import com.diego.playlistmaker.di.sharingModule
import com.diego.playlistmaker.di.useCaseModule
import com.diego.playlistmaker.di.viewModelModule
import com.diego.playlistmaker.settings.data.shared_prefs.SharedTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Устанавливаем тему
        SharedTheme.init(this)
        SharedTheme.applyTheme()

        // Инициализируем Koin
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                useCaseModule,
                settingsModule,
                sharingModule,
                viewModelModule
            )
        }
    }
}