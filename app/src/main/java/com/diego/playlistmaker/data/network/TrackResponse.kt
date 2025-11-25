package com.diego.playlistmaker.data.network

import com.diego.playlistmaker.domain.entity.Track

// модель ответа от API
class TrackResponse(
    val results: List<Track>       // список песен
)