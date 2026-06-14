package com.diego.playlistmaker.player.ui

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
import com.diego.playlistmaker.services.MusicPlayerManager
import com.diego.playlistmaker.services.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val playListInteractor: PlayListInteractor,
    private val trackInPlayListInteractor: TrackInPlayListInteractor
) : ViewModel() {

    private val _screenState = MutableStateFlow(PlayerScreenState())
    val screenState: StateFlow<PlayerScreenState> = _screenState

    private var currentTrack: Track? = null

    init {
        loadPlayLists()
        observePlaybackState()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            MusicPlayerManager.playbackState.collect { serviceState ->
                val playerState = mapServiceStateToPlayerState(serviceState)
                val currentPosition = MusicPlayerManager.currentPosition.value

                _screenState.value = _screenState.value.copy(
                    playerState = playerState,
                    currentPosition = currentPosition
                )
            }
        }

        viewModelScope.launch {
            MusicPlayerManager.currentPosition.collect { position ->
                _screenState.value = _screenState.value.copy(currentPosition = position)
            }
        }
    }

    private fun mapServiceStateToPlayerState(state: MusicService.PlaybackState): PlayerState {
        return when (state) {
            MusicService.PlaybackState.IDLE -> PlayerState.DEFAULT
            MusicService.PlaybackState.PREPARING -> PlayerState.PREPARING
            MusicService.PlaybackState.PREPARED -> PlayerState.PREPARED
            MusicService.PlaybackState.PLAYING -> PlayerState.PLAYING
            MusicService.PlaybackState.PAUSED -> PlayerState.PAUSED
            MusicService.PlaybackState.COMPLETED -> PlayerState.PREPARED
            MusicService.PlaybackState.ERROR -> PlayerState.ERROR
        }
    }

    fun setTrack(track: Track) {
        currentTrack = track

        // Создаем TrackInfo для UI
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

        // Запускаем сервис
        MusicPlayerManager.play(track.previewUrl, track.artistName, track.trackName)

        // Проверяем лайк и плейлист
        viewModelScope.launch(Dispatchers.IO) {
            val isLike = favoriteInteractor.isFavorite(track.trackId)
            val trackInPlayList = trackInPlayListInteractor.getTrackInPlayListByTrackId(track.trackId)
            val isPlayList = trackInPlayList != null
            val playListId = trackInPlayList?.playlistId ?: -1

            withContext(Dispatchers.Main) {
                _screenState.value = _screenState.value.copy(
                    trackInfo = trackInfo,
                    isLike = isLike,
                    isPlayList = isPlayList,
                    playListId = playListId
                )
            }
        }
    }

    fun play() {
        MusicPlayerManager.resume()
    }

    fun pause() {
        MusicPlayerManager.pause()
    }

    fun seekTo(position: Int) {
        MusicPlayerManager.seekTo(position)
    }

    fun release(){
        MusicPlayerManager.unbindService()
    }

    fun togglePlayPause() {
        if (MusicPlayerManager.isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    fun likeTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!favoriteInteractor.isFavorite(track.trackId)) {
                favoriteInteractor.insertTrack(track)
                withContext(Dispatchers.Main) {
                    _screenState.value = _screenState.value.copy(isLike = true)
                }
            } else {
                favoriteInteractor.deleteById(track.trackId)
                withContext(Dispatchers.Main) {
                    _screenState.value = _screenState.value.copy(isLike = false)
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
                playListInteractor.addTotalTimeMillis(playList.id, track.trackTimeMillis)
                trackInPlayListInteractor.insertTrackInPlayList(
                    TrackInPlayList(track = track, playlistId = playList.id)
                )
                withContext(Dispatchers.Main) {
                    _screenState.value = _screenState.value.copy(isPlayList = true)
                    onResult(true)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    fun getFormattedTime(timeMillis: Int): String {
        return MusicPlayerManager.getFormattedTime(timeMillis)
    }

    private fun loadPlayLists() {
        viewModelScope.launch(Dispatchers.IO) {
            playListInteractor.getAllPlayList().collect { lists ->
                withContext(Dispatchers.Main) {
                    _screenState.value = _screenState.value.copy(playListList = lists)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}