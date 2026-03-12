package com.diego.playlistmaker.media.domain.models

import com.diego.playlistmaker.search.domain.models.Track

data class TrackInPlayList(
    val track: Track,
    val playlistId: Int
)
