package com.diego.playlistmaker.media.ui.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.use_case.ImageStorageInteractor
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.sql.SQLException

class AddMediaPlayerViewModel(
    private val imageRepository: ImageStorageInteractor,
    private val playListRepository: PlayListInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(AddMediaPlayerState())
    var state: StateFlow<AddMediaPlayerState> = _state

    private var listPlayList: List<PlayList> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playListRepository.getAllPlayList().collect {
                listPlayList = it
            }
        }
    }

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

    suspend fun createPlayList(uri: Uri?, name: String, description: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                listPlayList.forEach {
                    if (it.name == name) {
                        updateState { state ->
                            state.copy(
                                nameError = true,
                                inputNameDrawable = R.drawable.bg_input_mediaplayer_error
                            )
                        }
                        return@withContext false
                    }
                }
                var newImagePath = ""
                uri?.let {
                    newImagePath = imageRepository.saveImage(it, name)
                    updateState { state -> state.copy(image = newImagePath) }
                    Log.d("TAG", "createPlayList: $newImagePath")
                }
                playListRepository.insertPlayList(
                    PlayList(
                        name = name,
                        description = description,
                        coverImagePath = newImagePath
                    )
                )
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } catch (e: SQLException) {
                e.printStackTrace()
                false
            }
            // CancellationException не перехватывается - пробрасывается автоматически
        }
    }

    private fun updateState(updater: (AddMediaPlayerState) -> AddMediaPlayerState) {
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}