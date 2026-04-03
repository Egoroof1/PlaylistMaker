package com.diego.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.R
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

    private var name: String = ""
    private var newImage = false

    fun setNewImage() {
        newImage = true
        if (name.isEmpty()) return
        updateState {
            it.copy(
                isBtnEnable = true,
                btnColor = R.color.color_bg_input_active
            )
        }
    }

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

    fun editTextName(text: String) {
        name = text
        if (text != _state.value.playList?.name && text.isNotEmpty()) {
            updateState {
                it.copy(
                    nameIsEnable = true,
                    isBtnEnable = true,
                    btnColor = R.color.color_bg_input_active,
                    inputNameDrawable = R.drawable.bg_input_mediaplayer_active
                )
            }
        } else {
            if (text.isNotEmpty() && newImage) return
            updateState {
                it.copy(
                    nameIsEnable = false,
                    isBtnEnable = false,
                    btnColor = R.color.color_bg_input_not_active,
                    inputNameDrawable = R.drawable.bg_input_mediaplayer
                )
            }
        }
    }

    fun editTextDescription(text: String) {
        if (text != _state.value.playList?.description && name.isNotEmpty()) {
            updateState {
                it.copy(
                    descIsEnable = true,
                    isBtnEnable = true,
                    btnColor = R.color.color_bg_input_active,
                    inputDescDrawable = R.drawable.bg_input_mediaplayer_active
                )
            }
        } else {
            if (newImage) return
            updateState {
                it.copy(
                    descIsEnable = false,
                    isBtnEnable = false,
                    btnColor = R.color.color_bg_input_not_active,
                    inputDescDrawable = R.drawable.bg_input_mediaplayer
                )
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