package com.diego.playlistmaker.models

// модель трека
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
) {
    fun info(): String{
        return "$trackId \n $trackName \n $artistName \n ${trackTimeMillis/1000} \n $artworkUrl100"
    }
}
// модель ответа от API
class TrackResponse(
    val resultCount: Int,         // количество результатов
    val results: List<Track>       // список песен
)
