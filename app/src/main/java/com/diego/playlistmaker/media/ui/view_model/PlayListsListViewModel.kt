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
                // Определяем, был ли добавлен новый плейлист
                val previousList = _state.value.playLists
                val newPlayList = lists.firstOrNull { newItem ->
                    previousList.none { oldItem -> oldItem.id == newItem.id }
                }

                updateState {
                    it.copy(
                        playLists = lists,
                        nameNewPlayList = newPlayList?.name ?: "" // Сохраняем имя нового плейлиста
                    )
                }

                // Сбрасываем nameNewPlayList после небольшой задержки
                if (newPlayList != null) {
                    resetNewPlayListName()
                }
            }
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