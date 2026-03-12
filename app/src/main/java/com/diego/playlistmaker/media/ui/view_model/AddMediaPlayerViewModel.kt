package com.diego.playlistmaker.media.ui.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.data.image_storage.ImageStorageRepository
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddMediaPlayerViewModel(
    private val repository: ImageStorageRepository
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
        updateState { it.copy(image = repository.saveImage(uri, name)) }
    }
    
    fun getAll(): List<Uri> {
        Log.d("TAG", "getAll: ${repository.getAllImages()}")
        return repository.getAllImages()
    }

    private fun updateState(updater: (AddMediaPlayerState) -> AddMediaPlayerState) {
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}