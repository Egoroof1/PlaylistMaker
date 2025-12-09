package com.diego.playlistmaker.config

import android.app.Application
import com.diego.playlistmaker.data.local.MyShared

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        MyShared.init(this)
        MyShared.applyTheme()
    }
}