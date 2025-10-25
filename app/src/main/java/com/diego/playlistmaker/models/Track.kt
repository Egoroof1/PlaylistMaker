package com.diego.playlistmaker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// модель трека

@Parcelize
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
) : Parcelable {

}
// модель ответа от API
class TrackResponse(
    val resultCount: Int,         // количество результатов
    val results: List<Track>       // список песен
)
