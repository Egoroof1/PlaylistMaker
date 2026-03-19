package com.diego.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.domain.use_case.TrackInPlayListInteractor
import com.diego.playlistmaker.media.ui.state.PlayListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayListViewModel(
    private val playListInteractor: PlayListInteractor,
    private val trackInPlayList: TrackInPlayListInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(PlayListState())
    var state: StateFlow<PlayListState> = _state



    suspend fun getPlayListBtId(playListId: Int): PlayList?{
        val playList: PlayList? = withContext(Dispatchers.IO) {
            playListInteractor.getPlayListById(playListId)
        }
        updateState { it.copy(playList = playList) }

        return playList
    }

    fun getTracksFromPlayList(playListId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            trackInPlayList.getAllTracksInPlayListByIdPlaylist(playListId).collect { tracks ->
                updateState {
                    it.copy(trackList = tracks)
                }
            }
        }
    }

    private fun updateState(updater: (PlayListState) -> PlayListState){
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}