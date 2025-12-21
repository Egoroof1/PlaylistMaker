package com.diego.playlistmaker.search.data.network.api

import com.diego.playlistmaker.search.data.models.TrackDto

// модель ответа от API
class TrackResponse(
    val resultCount: Int,         // количество результатов
    val results: List<TrackDto> = emptyList()       // список песен
)