package com.diego.playlistmaker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.diego.playlistmaker.R

class MusicService : Service(), MusicServiceController {
    private val binder = PlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var listener: PlayerStateListener? = null

    private var artistName: String = ""
    private var trackName: String = ""
    private var currentUrl: String = ""

    private var isPrepared = false

    inner class PlayerBinder : Binder() {
        fun getService(): MusicServiceController = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        showForeground()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            artistName = it.getStringExtra(EXTRA_ARTIST) ?: artistName
            trackName = it.getStringExtra(EXTRA_TRACK) ?: trackName
            currentUrl = it.getStringExtra(EXTRA_URL) ?: currentUrl
        }

        return START_NOT_STICKY
    }

    override fun prepare(url: String, artistName: String, trackName: String) {
        this.artistName = artistName
        this.trackName = trackName
        this.currentUrl = url

        mediaPlayer?.release()
        isPrepared = false

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                listener?.onPrepared()
            }
            setOnCompletionListener {
                listener?.onCompleted()
            }
            setOnErrorListener { _, _, _ ->
                listener?.onError()
                true
            }
            prepareAsync()
        }
    }

    override fun play() {
        if (isPrepared && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
            listener?.onPlaying()
        }
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            listener?.onPaused()
        }
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun currentPositionMs(): Int = mediaPlayer?.currentPosition ?: 0

    override fun getDuration(): Int = mediaPlayer?.duration ?: 0

    override fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
    }

    override fun setOnPlayerStateListener(listener: PlayerStateListener) {
        this.listener = listener
    }

    override fun showForeground() {
        val notification = createNotification().build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, 0)
        }
    }

    override fun hideForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        if (!isPlaying()) {
            stopSelf()
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        val isPlaying = isPlaying()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.pr_icon)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    companion object {
        const val CHANNEL_ID = "player_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_TRACK = "extra_track"
        const val EXTRA_URL = "extra_url"
    }
}