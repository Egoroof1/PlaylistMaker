package com.diego.playlistmaker.models

// модель трека

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
) {
    fun info(): String{
        return "Track(trackId=$trackId, trackName='$trackName', artistName='$artistName', " +
                "collectionName='$collectionName', releaseDate='$releaseDate', " +
                "primaryGenreName='$primaryGenreName', country='$country', trackTimeMillis=$trackTimeMillis"
    }
}
// модель ответа от API
class TrackResponse(
    val resultCount: Int,         // количество результатов
    val results: List<Track>       // список песен
)
