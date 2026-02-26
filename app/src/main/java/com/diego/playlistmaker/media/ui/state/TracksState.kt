package com.diego.playlistmaker.media.ui.state

import com.diego.playlistmaker.search.domain.models.Track

data class TracksState(
    val tracksList: List<Track> = emptyList()
)
