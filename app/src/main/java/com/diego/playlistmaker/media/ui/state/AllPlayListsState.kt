package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.media.domain.models.PlayList

data class AllPlayListsState(
    val playLists: List<PlayList> = emptyList(),
    val nameNewPlayList: String = ""
)
