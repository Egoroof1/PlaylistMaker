package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.search.domain.models.Track

data class PlayListState(
    val playList: PlayList? = null,
    val trackList: List<Track> = emptyList()
)
