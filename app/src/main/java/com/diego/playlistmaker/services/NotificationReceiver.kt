package com.diego.playlistmaker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            MusicService.ACTION_PLAY -> MusicPlayerManager.resume()
            MusicService.ACTION_PAUSE -> MusicPlayerManager.pause()
            MusicService.ACTION_CLOSE -> MusicPlayerManager.stopAndClose()
        }
    }
}