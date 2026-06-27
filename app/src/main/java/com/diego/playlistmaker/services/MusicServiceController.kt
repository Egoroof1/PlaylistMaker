package com.diego.playlistmaker.services

interface MusicServiceController {
    fun prepare(url: String, artistName: String, trackName: String)
    fun play()
    fun pause()
    fun isPlaying(): Boolean
    fun currentPositionMs(): Int
    fun getDuration(): Int
    fun seekTo(positionMs: Int)
    fun setOnPlayerStateListener(listener: PlayerStateListener)
    fun showForeground()
    fun hideForeground()
}

interface PlayerStateListener {
    fun onPlaying()
    fun onPaused()
    fun onPrepared()
    fun onCompleted()
    fun onError()
}