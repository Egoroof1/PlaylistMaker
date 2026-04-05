package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.domain.models.PlayList

data class AddMediaPlayerState(
    val playList: PlayList? = null,
    val image: String = "",
    val nameError: Boolean = false,
    val nameIsEnable: Boolean = false,
    val descIsEnable: Boolean = false,
    val isBtnEnable: Boolean = false,
    val btnColor: Int = R.color.color_bg_input_not_active,
    val inputNameDrawable: Int = R.drawable.bg_input_mediaplayer,
    val inputDescDrawable: Int = R.drawable.bg_input_mediaplayer
)
