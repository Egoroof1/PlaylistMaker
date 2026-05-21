package com.diego.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.diego.playlistmaker.player.models.PlayerScreenState
import com.diego.playlistmaker.player.models.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerService : Service(), AudioPlayerControl {

    private val binder = PlayerServiceBinder()

    private val _playerState = MutableStateFlow(PlayerScreenState())
    val playerState = _playerState.asStateFlow()

    private var songUrl = ""

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    private fun updateState(updater: (PlayerScreenState) -> PlayerScreenState){
        val currentState = _playerState.value
        _playerState.value = updater(currentState)
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(200L)
                updateState {
                    it.copy(
                        playerState = PlayerState.PLAYING,
                        currentPosition = getCurrentPlayerPosition().toInt()
                    )
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra("song_url") ?: ""
        initMediaPlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    // Методы управления Media Player

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) return

        mediaPlayer?.setDataSource(songUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            updateState {
                it.copy(
                    playerState = PlayerState.PREPARED
                )
            }
        }
        mediaPlayer?.setOnCompletionListener {
            updateState {
                it.copy(
                    playerState = PlayerState.PREPARED
                )
            }
        }
    }

    override fun getPlayerState(): StateFlow<PlayerScreenState> {
        return playerState
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        updateState {
            it.copy(
                playerState = PlayerState.PLAYING,
                currentPosition = getCurrentPlayerPosition().toInt()
            )
        }
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        updateState {
            it.copy(
                playerState = PlayerState.PAUSED,
                currentPosition = getCurrentPlayerPosition().toInt()
            )
        }
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        updateState {
            it.copy(
                playerState = PlayerState.DEFAULT
            )
        }
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition) ?: "00:00"
    }

    // Binder

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }
}