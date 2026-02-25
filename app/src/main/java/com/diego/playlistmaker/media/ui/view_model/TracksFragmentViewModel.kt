package com.diego.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.FavoriteRepositoryUseCase
import com.diego.playlistmaker.media.domain.use_case.HistoryRepositoryUseCase
import com.diego.playlistmaker.media.ui.state.TracksState
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TracksFragmentViewModel(
    private val repository: FavoriteRepositoryUseCase,
    private val historyRepository: HistoryRepositoryUseCase
) : ViewModel() {

    private val _tracksState = MutableStateFlow(TracksState())
    val tracksState: StateFlow<TracksState> = _tracksState

    init {
        observeFavoriteTracks()
    }

    private fun observeFavoriteTracks() {

        viewModelScope.launch {
            repository.favoriteTracks().collect { tracks ->
                updateState { currentState ->
                    currentState.copy(
                        tracksList = tracks
                    )
                }
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            val tracksHistory = historyRepository.getHistoryTracks()

            if (tracksHistory.contains<Any>(track)){
                historyRepository.deleteById(track.trackId)
                historyRepository.insertTrack(track)
                return@launch
            }
            if (tracksHistory.size < 10){
                historyRepository.insertTrack(track)
            } else {
                historyRepository.deleteFirstTrack()
                historyRepository.insertTrack(track)
            }

        }
    }

    private fun updateState(updater: (TracksState) -> TracksState) {
        val currentState = _tracksState.value
        _tracksState.value = updater(currentState)
    }
}