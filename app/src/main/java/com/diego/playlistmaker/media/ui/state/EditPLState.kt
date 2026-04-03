package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.media.domain.models.PlayList

data class EditPLState(
    val playList: PlayList? = null,
    val nameIsEnable: Boolean = false,
    val descIsEnable: Boolean = false
)
