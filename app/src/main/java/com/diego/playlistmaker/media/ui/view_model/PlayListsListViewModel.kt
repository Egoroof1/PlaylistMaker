package com.diego.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.ui.state.AllPlayListsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayListsListViewModel(
    private val playListInteractor: PlayListInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(AllPlayListsState())
    val state: StateFlow<AllPlayListsState> = _state

    init {
        observeAllPlayList()
    }

    private fun observeAllPlayList() {
        viewModelScope.launch(Dispatchers.IO) {
            playListInteractor.getAllPlayList().collect { lists ->
                val previousList = _state.value.playLists

                // Находим новый плейлист
                val newPlayList = lists.firstOrNull { newItem ->
                    previousList.none { oldItem -> oldItem.id == newItem.id }
                }

                // Находим удаленный плейлист
                val deletedPlayList = previousList.firstOrNull { oldItem ->
                    lists.none { newItem -> newItem.id == oldItem.id }
                }

                updateState {
                    it.copy(
                        playLists = lists,
                        nameNewPlayList = newPlayList?.name ?: "",
                        nameDeletedPlayList = deletedPlayList?.name ?: ""
                    )
                }

                // Сбрасываем имя нового плейлиста через задержку
                if (newPlayList != null) {
                    resetNewPlayListName()
                }

                // Сбрасываем имя удаленного плейлиста через задержку
                if (deletedPlayList != null) {
                    resetDeletedPlayListName()
                }
            }
        }
    }

    private fun resetDeletedPlayListName() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(100)
            updateState { it.copy(nameDeletedPlayList = "") }
        }
    }

    private fun resetNewPlayListName() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(100)
            updateState { it.copy(nameNewPlayList = "") }
        }
    }

    private fun updateState(updater: (AllPlayListsState) -> AllPlayListsState) {
        val currentState = _state.value
        _state.value = updater(currentState)
    }

    // Метод для ручного сброса (можно вызвать из фрагмента)
    fun resetNewPlayListNameManually() {
        updateState { it.copy(nameNewPlayList = "") }
    }
}