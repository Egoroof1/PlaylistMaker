package com.diego.playlistmaker.config

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MyShared.init(this)
        MyShared.applyTheme()
    }
}