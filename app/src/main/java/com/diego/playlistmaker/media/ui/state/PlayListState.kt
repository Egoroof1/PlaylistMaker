package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.media.domain.models.PlayList

data class PlayListState(
    val playList: PlayList? = null
)
