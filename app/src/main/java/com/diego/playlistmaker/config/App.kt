package com.diego.playlistmaker.config

import android.app.Application
import com.diego.playlistmaker.domain.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    private fun applyTheme() {
        val applyThemeUseCase = Creator.provideApplyThemeUseCase(this)
        applyThemeUseCase.execute()
    }
}