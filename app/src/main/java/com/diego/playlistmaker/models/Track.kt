package com.diego.playlistmaker.models

// модель трека
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)
// модель ответа от API
class TrackResponse(
    val resultCount: Int,         // количество результатов
    val results: List<Track>       // список песен
)
