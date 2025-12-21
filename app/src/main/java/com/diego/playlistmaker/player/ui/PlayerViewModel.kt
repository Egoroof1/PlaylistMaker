package com.diego.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diego.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class PlayerViewModel : ViewModel() {

    // LiveData для состояния плеера
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    // LiveData для текущего времени трека
    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    // LiveData для информации о треке
    private val _trackInfo = MutableLiveData<TrackInfo>()
    val trackInfo: LiveData<TrackInfo> = _trackInfo

    // MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    // Timer для обновления прогресса
    private var progressTimer: Timer? = null

    // Флаг для отслеживания подготовки плеера
    private var isPrepared = false

    fun setTrack(track: Track) {
        // Создаем TrackInfo из Track
        val trackInfo = TrackInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            albumName = track.collectionName ?: "",
            genre = track.primaryGenreName,
            country = track.country,
            releaseYear = track.releaseDate.split("-").firstOrNull() ?: "",
            artworkUrl = track.artworkUrl100.replaceAfterLast("/", "512x512.jpg"),
            originalArtworkUrl = track.artworkUrl100,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl
        )
        _trackInfo.postValue(trackInfo)
    }

    fun preparePlayer(previewUrl: String) {
        releasePlayer() // Освобождаем предыдущий MediaPlayer

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
                _playerState.postValue(PlayerState.PREPARED)
            }

            setOnCompletionListener {
                stopProgressTimer()
                _playerState.postValue(PlayerState.PREPARED)
                _currentPosition.postValue(0)
            }

            setOnErrorListener { _, what, extra ->
                _playerState.postValue(PlayerState.ERROR)
                false
            }
        }
    }

    fun play() {
        if (isPrepared && _playerState.value != PlayerState.PLAYING) {
            mediaPlayer?.start()
            _playerState.postValue(PlayerState.PLAYING)
            startProgressTimer()
        }
    }

    fun pause() {
        if (_playerState.value == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            _playerState.postValue(PlayerState.PAUSED)
            stopProgressTimer()
        }
    }

    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.PLAYING -> pause()
            PlayerState.PREPARED, PlayerState.PAUSED -> play()
            else -> Unit
        }
    }

    //перемотка
    fun seekTo(position: Int) {
        if (isPrepared && position in 0..(mediaPlayer?.duration ?: 0)) {
            mediaPlayer?.seekTo(position)
            _currentPosition.postValue(position)
        }
    }

    private fun startProgressTimer() {
        stopProgressTimer() // Останавливаем предыдущий таймер, если есть

        Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    if (isPrepared) {
                        val currentPos = mediaPlayer?.currentPosition ?: 0
                        _currentPosition.postValue(currentPos)
                    }
                }
            }, 0, 500) // Обновляем каждые 500ms
        }.also { progressTimer = it }
    }

    private fun stopProgressTimer() {
        progressTimer?.cancel()
        progressTimer = null
    }

    fun getFormattedTime(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        stopProgressTimer()
    }

    fun releasePlayer() {
        stopProgressTimer()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        _playerState.postValue(PlayerState.DEFAULT)
    }

    // Data class для хранения информации о треке
    data class TrackInfo(
        val trackName: String,
        val artistName: String,
        val albumName: String,
        val genre: String,
        val country: String,
        val releaseYear: String,
        val artworkUrl: String,
        val originalArtworkUrl: String,
        val trackTimeMillis: Long,
        val previewUrl: String
    )

    // Enum для состояний плеера
    enum class PlayerState {
        DEFAULT,      // Начальное состояние
        PREPARING,    // Готовится
        PREPARED,     // Готов к воспроизведению
        PLAYING,      // Воспроизводится
        PAUSED,       // На паузе
        ERROR         // Ошибка
    }
}