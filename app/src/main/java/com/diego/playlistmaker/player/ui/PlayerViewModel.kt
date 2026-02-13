package com.diego.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.player.models.PlayerState
import com.diego.playlistmaker.player.models.TrackInfo
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel : ViewModel() {

    // LiveData для состояния плеера
    private val _playerState = MutableLiveData(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    // LiveData для текущего времени трека
    private val _currentPosition = MutableLiveData(0)
    val currentPosition: LiveData<Int> = _currentPosition

    // LiveData для информации о треке
    private val _trackInfo = MutableLiveData<TrackInfo>()
    val trackInfo: LiveData<TrackInfo> = _trackInfo

    // MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    // Job для обновления прогресса (замена Timer)
    private var progressJob: Job? = null

    // Флаг для отслеживания подготовки плеера
    private var isPrepared = false

    fun setTrack(track: Track) {
        // Создаем TrackInfo из Track
        val trackInfo = TrackInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            albumName = track.collectionName,
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
                // Принудительно перематываем в начало
                mediaPlayer?.seekTo(0)
                // Обновляем состояние
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
            mediaPlayer?.currentPosition?.let { position ->
                _currentPosition.postValue(position)
            }
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
        stopProgressTimer() // Останавливаем предыдущую корутину, если есть

        progressJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive && _playerState.value == PlayerState.PLAYING) {
                _currentPosition.value = mediaPlayer?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    private fun stopProgressTimer() {
        progressJob?.cancel()
        progressJob = null
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
}