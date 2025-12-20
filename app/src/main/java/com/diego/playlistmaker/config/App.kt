package com.diego.playlistmaker.config

import android.app.Application
import com.diego.playlistmaker.creator.Creator
import com.diego.playlistmaker.settings.data.shared_prefs.SharedTheme

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        SharedTheme.init(this)
        SharedTheme.applyTheme()
    }
}