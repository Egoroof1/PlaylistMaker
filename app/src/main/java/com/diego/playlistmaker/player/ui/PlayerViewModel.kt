package com.diego.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.models.TrackInPlayList
import com.diego.playlistmaker.media.domain.use_case.FavoriteInteractor
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.domain.use_case.TrackInPlayListInteractor
import com.diego.playlistmaker.player.models.PlayerScreenState
import com.diego.playlistmaker.player.models.PlayerState
import com.diego.playlistmaker.player.models.TrackInfo
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val repositoryUseCase: FavoriteInteractor,
    private val playListInteractor: PlayListInteractor,
    private val trackInPlayListInteractor: TrackInPlayListInteractor
) : ViewModel() {

    private val _screenState = MutableStateFlow(PlayerScreenState())
    val screenState: StateFlow<PlayerScreenState> = _screenState

    // MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    // Job для обновления прогресса (замена Timer)
    private var progressJob: Job? = null

    // Флаг для отслеживания подготовки плеера
    private var isPrepared = false
    private var currentPlayList: PlayList? = null

    init {
        loadPlayLists()
    }

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

        viewModelScope.launch(Dispatchers.IO) {
            val isLike = repositoryUseCase.isFavorite(track.trackId)
            val trackInPlayList: TrackInPlayList? =
                trackInPlayListInteractor.getTrackInPlayListByTrackId(track.trackId)
            val isPlayList = trackInPlayList != null
            val playListId = trackInPlayList?.playlistId ?: -1

            currentPlayList = playListInteractor.getPlayListById(playListId)

            withContext(Dispatchers.Main) {
                if (isPlayList) {
                    updateState {
                        it.copy(
                            playListId = playListId
                        )
                    }
                }

                updateState {
                    it.copy(
                        trackInfo = trackInfo,
                        isLike = isLike,
                        isPlayList = isPlayList
                    )
                }
            }
        }

        preparePlayer(track.previewUrl)
    }

    private fun updateState(updater: (PlayerScreenState) -> PlayerScreenState) {
        val currentState = _screenState.value
        _screenState.value = updater(currentState)
    }

    fun likeTrack(track: Track) {

        viewModelScope.launch(Dispatchers.IO) {
            if (!repositoryUseCase.isFavorite(track.trackId)) {
                repositoryUseCase.insertTrack(track = track)

                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(isLike = true)
                    }
                }
            } else {
                repositoryUseCase.deleteById(trackId = track.trackId)

                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(isLike = false)
                    }
                }
            }
        }
    }

    private fun preparePlayer(previewUrl: String) {
        releasePlayer() // Освобождаем предыдущий MediaPlayer

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
                updateState { it.copy(playerState = PlayerState.PREPARED) }
            }

            setOnCompletionListener {
                stopProgressTimer()
                // Принудительно перематываем в начало
                mediaPlayer?.seekTo(0)
                // Обновляем состояние
                updateState {
                    it.copy(
                        playerState = PlayerState.PREPARED,
                        currentPosition = 0
                    )
                }
            }

            setOnErrorListener { _, what, extra ->
                updateState { it.copy(playerState = PlayerState.ERROR) }
                false
            }
        }
    }

    fun play() {
        if (isPrepared && _screenState.value.playerState != PlayerState.PLAYING) {
            mediaPlayer?.start()
            updateState { it.copy(playerState = PlayerState.PLAYING) }
            startProgressTimer()
        }
    }

    fun pause() {
        if (_screenState.value.playerState == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            updateState { it.copy(playerState = PlayerState.PAUSED) }
            stopProgressTimer()
            mediaPlayer?.currentPosition?.let { position ->
                updateState { it.copy(currentPosition = position) }
            }
        }
    }

    fun togglePlayPause() {
        when (_screenState.value.playerState) {
            PlayerState.PLAYING -> pause()
            PlayerState.PREPARED, PlayerState.PAUSED -> play()
            else -> Unit
        }
    }

    private fun startProgressTimer() {
        stopProgressTimer() // Останавливаем предыдущую корутину, если есть

        progressJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive && _screenState.value.playerState == PlayerState.PLAYING) {
                updateState { it.copy(currentPosition = mediaPlayer?.currentPosition ?: 0) }
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
        updateState { it.copy(playerState = PlayerState.DEFAULT) }
    }

    private fun loadPlayLists() {
        viewModelScope.launch(Dispatchers.IO) {
            playListInteractor.getAllPlayList().collect { lists ->
                withContext(Dispatchers.Main) {
                    updateState { it.copy(playListList = lists) }
                }
            }
        }
    }

    fun addTrackToPlayList(playList: PlayList, track: Track, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val tracksInPlayList = trackInPlayListInteractor
                .getAllTracksInPlayListByIdPlaylist(playList.id)
                .first()

            val trackExists = tracksInPlayList.any { it.trackId == track.trackId }

            if (!trackExists) {
                playListInteractor.incrementTracksCount(playListId = playList.id)
                playListInteractor.addTotalTimeMillis(
                    playListId = playList.id,
                    track.trackTimeMillis
                )

                trackInPlayListInteractor.insertTrackInPlayList(
                    TrackInPlayList(
                        track = track,
                        playlistId = playList.id
                    )
                )

                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(
                            isPlayList = true
                        )
                    }
                    onResult(true)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }
}
