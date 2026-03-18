package com.diego.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.ui.state.AllPlayListsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayListsFragmentViewModel(
    private val playListInteractor: PlayListInteractor
) : ViewModel() {

    private var _state = MutableStateFlow(AllPlayListsState())
    val state: StateFlow<AllPlayListsState> = _state

    init {
        observeAllPlayList()
    }

    private fun observeAllPlayList() {
        viewModelScope.launch {
//            playListInteractor.getAllPlayList().collect { lists ->
//                updateState { state ->
//                    state.copy(playLists = lists)
//                }
//            }
        }
    }

    private fun updateState(updater: (AllPlayListsState) -> AllPlayListsState){
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}