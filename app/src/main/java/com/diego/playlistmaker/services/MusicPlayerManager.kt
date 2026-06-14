package com.diego.playlistmaker.services

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MusicPlayerManager {
    private var service: MusicService? = null
    private var appContext: Context? = null

    // Потоки состояния
    private val _playbackState = MutableStateFlow(MusicService.PlaybackState.IDLE)
    val playbackState: StateFlow<MusicService.PlaybackState> = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private var currentTrackUrl: String? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun bindService(service: MusicService) {
        this.service = service
        service.onStateChanged = { state, position, dur ->
            _playbackState.value = state
            _currentPosition.value = position
            _duration.value = dur
        }
    }

    fun setAppForegroundState(isForeground: Boolean) {
        service?.setAppForegroundState(isForeground)
    }

    fun unbindService() {
        service?.onStateChanged = null
        service = null
    }

    fun play(trackUrl: String, artistName: String, trackName: String) {
        currentTrackUrl = trackUrl
        appContext?.let { ctx ->
            val intent = Intent(ctx, MusicService::class.java).apply {
                putExtra(MusicService.EXTRA_SONG_URL, trackUrl)
                putExtra(MusicService.EXTRA_ARTIST_NAME, artistName)
                putExtra(MusicService.EXTRA_TRACK_NAME, trackName)
            }
            ctx.startForegroundService(intent)
        }
    }

    fun resume() {
        service?.play()
    }

    fun pause() {
        service?.pause()
    }

    fun seekTo(position: Int) {
        service?.seekTo(position)
    }

    fun stopAndClose() {
        service?.stopSelf()
        currentTrackUrl = null
        _playbackState.value = MusicService.PlaybackState.IDLE
        _currentPosition.value = 0
        _duration.value = 0
    }

    fun release() {
        service = null
        currentTrackUrl = null
    }

    fun isPlaying(): Boolean {
        return _playbackState.value == MusicService.PlaybackState.PLAYING
    }

    fun getFormattedTime(millis: Int): String {
        return service?.getFormattedTime(millis) ?: "00:00"
    }
}