package com.diego.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.diego.playlistmaker.R
import com.diego.playlistmaker.main.ui.root.RootActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var isPrepared = false
    private var currentSongUrl: String? = null

    private var currentArtistName: String = ""
    private var currentTrackName: String = ""

    // Текущее состояние
    private var _playbackState = PlaybackState.IDLE
    private var _currentPosition = 0
    private var _duration = 0

    private var isAppInForeground = true

    // Коллбек для MusicPlayerManager
    var onStateChanged: ((PlaybackState, Int, Int) -> Unit)? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val url = it.getStringExtra(EXTRA_SONG_URL)
            currentArtistName = it.getStringExtra(EXTRA_ARTIST_NAME) ?: ""
            currentTrackName = it.getStringExtra(EXTRA_TRACK_NAME) ?: ""

            url?.let { songUrl ->
                if (currentSongUrl != songUrl) {
                    preparePlayer(url)
                }
            }
        }

        return START_STICKY
    }

    fun setAppForegroundState(isForeground: Boolean) {
        isAppInForeground = isForeground
        updateNotificationVisibility()
    }

    private fun updateNotificationVisibility() {
        if (isAppInForeground) {
            // Приложение на переднем плане - убираем уведомление
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            // Приложение свернуто - показываем уведомление
            if (isPrepared && _playbackState != PlaybackState.IDLE) {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        }
    }

    private fun preparePlayer(songUrl: String) {
        currentSongUrl = songUrl
        releasePlayer()

        updateState(PlaybackState.PREPARING, 0, 0)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(songUrl)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
                _duration = it.duration
                updateState(PlaybackState.PREPARED, 0, _duration)

                if (!isAppInForeground) {
                    startForeground(NOTIFICATION_ID, createNotification())
                }
            }

            setOnCompletionListener {
                stopProgressTimer()
                mediaPlayer?.seekTo(0)
                updateState(PlaybackState.COMPLETED, 0, _duration)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            setOnErrorListener { _, _, _ ->
                updateState(PlaybackState.ERROR, 0, 0)
                true
            }
        }
    }

    fun play() {
        if (isPrepared && _playbackState != PlaybackState.PLAYING) {
            mediaPlayer?.start()
            updateState(PlaybackState.PLAYING, mediaPlayer?.currentPosition ?: 0, _duration)
            startProgressTimer()
            // Обновляем уведомление только если приложение свернуто
            if (!isAppInForeground) {
                updateNotification()

            }
            updateNotificationVisibility()
        }
    }

    fun pause() {
        if (_playbackState == PlaybackState.PLAYING) {
            mediaPlayer?.pause()
            updateState(PlaybackState.PAUSED, mediaPlayer?.currentPosition ?: 0, _duration)
            stopProgressTimer()
            // Обновляем уведомление только если приложение свернуто
            if (!isAppInForeground) {
                updateNotification()

            }
            updateNotificationVisibility()
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition = position
        onStateChanged?.invoke(_playbackState, position, _duration)
    }

    private fun startProgressTimer() {
        stopProgressTimer()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && _playbackState == PlaybackState.PLAYING) {
                delay(500)
                mediaPlayer?.currentPosition?.let { pos ->
                    if (pos != _currentPosition) {
                        _currentPosition = pos
                        onStateChanged?.invoke(_playbackState, pos, _duration)
                    }
                }
            }
        }
    }

    private fun stopProgressTimer() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updateState(state: PlaybackState, position: Int, dur: Int) {
        _playbackState = state
        _currentPosition = position
        _duration = dur
        onStateChanged?.invoke(state, position, dur)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Музыкальный плеер",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Управление воспроизведением музыки"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val isPlaying = _playbackState == PlaybackState.PLAYING

        // Intent для открытия приложения
        val openIntent = Intent(this, RootActivity::class.java).apply {
            putExtra(EXTRA_SONG_URL, currentSongUrl)
            putExtra(EXTRA_ARTIST_NAME, currentArtistName)
            putExtra(EXTRA_TRACK_NAME, currentTrackName)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$currentArtistName - $currentTrackName")
            .setSmallIcon(R.drawable.pr_icon)
            .setContentIntent(openPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun releasePlayer() {
        stopProgressTimer()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    fun getFormattedTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_SONG_URL = "song_url"
        const val EXTRA_TRACK_NAME = "track_name"
        const val EXTRA_ARTIST_NAME = "artist_name"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_CLOSE = "ACTION_CLOSE"
    }

    enum class PlaybackState {
        IDLE,       // Начальное
        PREPARING,  // Загрузка
        PREPARED,   // Готов к воспроизведению
        PLAYING,    // Играет
        PAUSED,     // На паузе
        COMPLETED,  // Завершен
        ERROR       // Ошибка
    }
}