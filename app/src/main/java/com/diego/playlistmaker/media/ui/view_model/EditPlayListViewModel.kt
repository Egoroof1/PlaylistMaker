package com.diego.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.ImageStorageInteractor
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.ui.state.EditPLState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPlayListViewModel(
    private val playListInteractor: PlayListInteractor,
    private val imageInteractor: ImageStorageInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(EditPLState())
    var state: StateFlow<EditPLState> = _state

    fun setPlayList(playListId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playListInteractor.getPlayListById(playListId).collect { playList ->
                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(
                            playList = playList
                        )
                    }
                }
            }
        }
    }

    fun updatePlayList(id: Int, name: String, desc: String, imagePath: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            var storageImagePath = state.value.playList?.coverImagePath ?: ""
            if (imagePath.toString().isNotEmpty()) {
                imageInteractor.deleteImage(state.value.playList?.coverImagePath ?: "")
                storageImagePath = imageInteractor.saveImage(imagePath, name)
            }

            playListInteractor.updatePlayList(id, name, desc, storageImagePath)
        }
    }

    private fun updateState(updater: (EditPLState) -> EditPLState) {
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}