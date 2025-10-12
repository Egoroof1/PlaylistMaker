package com.diego.playlistmaker.config

import android.app.Application
import com.diego.playlistmaker.services.MyShared

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MyShared.init(this)
    }
}