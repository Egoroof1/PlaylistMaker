package com.diego.playlistmaker.media.ui.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.data.image_storage.ImageStorageRepository
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddMediaPlayerViewModel(
    private val imageRepository: ImageStorageRepository,
    private val playListRepository: PlayListInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(AddMediaPlayerState())
    var state: StateFlow<AddMediaPlayerState> = _state

    fun editTextName(text: String) {
        if (text.isNotEmpty()){
            updateState { it.copy(
                nameIsEnable = true,
                isBtnEnable = true,
                btnColor = R.color.color_bg_input_active,
                inputNameDrawable = R.drawable.bg_input_mediaplayer_active)
            }
        } else {
            updateState { it.copy(
                nameIsEnable = false,
                isBtnEnable = true,
                btnColor = R.color.color_bg_input_not_active,
                inputNameDrawable = R.drawable.bg_input_mediaplayer)
            }
        }
    }

    fun editTextDescription(text: String) {
        if (text.isNotEmpty()){
            updateState { it.copy(
                descIsEnable = true,
                inputDescDrawable = R.drawable.bg_input_mediaplayer_active
                )
            }
        } else {
            updateState { it.copy(
                descIsEnable = false,
                inputDescDrawable = R.drawable.bg_input_mediaplayer)
            }
        }
    }

    fun saveImage(uri: Uri, name: String){
        val newImage = imageRepository.saveImage(uri, name)
        updateState { it.copy(image = newImage) }
        Log.d("TAG", "saveImage: $newImage")
    }

    fun savePlayList(playList: PlayList){
        var image = ""
        if (state.value.image.isNotEmpty()){
            image = state.value.image
        }
        viewModelScope.launch(Dispatchers.IO){
            playListRepository.insertPlayList(playList.copy(coverImagePath = image))
        }
    }

    private fun updateState(updater: (AddMediaPlayerState) -> AddMediaPlayerState) {
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}