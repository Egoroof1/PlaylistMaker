package com.diego.playlistmaker.player.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
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
import com.diego.playlistmaker.services.MusicService
import com.diego.playlistmaker.services.MusicServiceController
import com.diego.playlistmaker.services.PlayerStateListener
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
    private val context: Context,
    private val repositoryUseCase: FavoriteInteractor,
    private val playListInteractor: PlayListInteractor,
    private val trackInPlayListInteractor: TrackInPlayListInteractor
) : ViewModel(), PlayerStateListener {

    private val _screenState = MutableStateFlow(PlayerScreenState())
    val screenState: StateFlow<PlayerScreenState> = _screenState

    private var musicService: MusicServiceController? = null
    private var isBound = false
    private var currentTrack: Track? = null
    private var progressJob: Job? = null
    private var isPreparing = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicService.PlayerBinder
            musicService = binder?.getService()
            isBound = true

            musicService?.setOnPlayerStateListener(this@PlayerViewModel)

            // Если есть трек, но сервис еще не подготовлен
            currentTrack?.let { track ->
                if (!isPreparing && musicService?.getDuration() == 0) {
                    prepareTrack(track)
                } else {
                    // Восстанавливаем состояние после переподключения
                    restoreState()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    init {
        loadPlayLists()
        // Запускаем сервис сразу при создании ViewModel
        startServiceAndBind()
    }

    private fun startServiceAndBind() {
        val intent = Intent(context, MusicService::class.java).apply {
            currentTrack?.let { track ->
                putExtra(MusicService.EXTRA_ARTIST, track.artistName)
                putExtra(MusicService.EXTRA_TRACK, track.trackName)
                putExtra(MusicService.EXTRA_URL, track.previewUrl)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun prepareTrack(track: Track) {
        isPreparing = true
        updateState { it.copy(playerState = PlayerState.PREPARING) }

        musicService?.prepare(
            track.previewUrl,
            track.artistName,
            track.trackName
        )
    }

    private fun restoreState() {
        // Восстанавливаем состояние плеера при переподключении
        val isPlaying = musicService?.isPlaying() ?: false
        val position = musicService?.currentPositionMs() ?: 0

        if (isPlaying) {
            updateState {
                it.copy(
                    playerState = PlayerState.PLAYING,
                    currentPosition = position
                )
            }
            startProgressTimer()
        } else if (position > 0) {
            updateState {
                it.copy(
                    playerState = PlayerState.PAUSED,
                    currentPosition = position
                )
            }
        }
    }

    fun releaseAndStopPlayer() {
        // Останавливаем прогресс-таймер
        stopProgressTimer()

        // Останавливаем воспроизведение и освобождаем MediaPlayer
        musicService?.pause()
        musicService?.hideForeground()

        // Сбрасываем состояние
        updateState {
            it.copy(
                playerState = PlayerState.DEFAULT,
                currentPosition = 0
            )
        }
    }

    fun pauseProgressTimer() {
        stopProgressTimer()
    }

    fun setTrack(track: Track) {
        currentTrack = track

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

            withContext(Dispatchers.Main) {
                updateState {
                    it.copy(
                        trackInfo = trackInfo,
                        isLike = isLike,
                        isPlayList = isPlayList
                    )
                }
            }
        }

        if (isBound && musicService != null) {
            prepareTrack(track)
        }
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

    fun play() {
        musicService?.play()
        startProgressTimer()
    }

    fun pause() {
        musicService?.pause()
        stopProgressTimer()
        musicService?.currentPositionMs()?.let { position ->
            updateState { it.copy(currentPosition = position) }
        }
    }

    fun onAppForeground() {
        // Приложение в фореграунде
        val currentState = _screenState.value.playerState

        if (currentState == PlayerState.PLAYING) {
            // Обновляем позицию из сервиса
            musicService?.currentPositionMs()?.let { position ->
                updateState { it.copy(currentPosition = position) }
            }
            // Перезапускаем таймер
            startProgressTimer()
        }

        // Скрываем уведомление
        musicService?.hideForeground()
    }

    fun onAppBackground() {
        // Приложение свернуто
        val currentState = _screenState.value.playerState

        if (currentState == PlayerState.PLAYING || currentState == PlayerState.PAUSED) {
            // Останавливаем таймер, чтобы не тратить ресурсы
            stopProgressTimer()
            // Показываем уведомление
            musicService?.showForeground()
        }
    }

    private fun startProgressTimer() {
        stopProgressTimer()

        progressJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive && _screenState.value.playerState == PlayerState.PLAYING) {
                musicService?.currentPositionMs()?.let { position ->
                    updateState { it.copy(currentPosition = position) }
                }
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
        stopProgressTimer()
        if (isBound) {
            try {
                context.unbindService(serviceConnection)
            } catch (e: Exception) {
                // Игнорируем ошибку, если сервис уже отвязан
            }
            isBound = false
        }
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
                        it.copy(isPlayList = true)
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

    // PlayerStateListener implementation
    override fun onPlaying() {
        isPreparing = false
        updateState { it.copy(playerState = PlayerState.PLAYING) }
    }

    override fun onPaused() {
        isPreparing = false
        updateState { it.copy(playerState = PlayerState.PAUSED) }
    }

    override fun onPrepared() {
        isPreparing = false
        updateState { it.copy(playerState = PlayerState.PREPARED) }
    }

    override fun onCompleted() {
        stopProgressTimer()
        updateState {
            it.copy(
                playerState = PlayerState.PREPARED,
                currentPosition = 0
            )
        }
    }

    override fun onError() {
        isPreparing = false
        stopProgressTimer()
        updateState { it.copy(playerState = PlayerState.ERROR) }
    }
}