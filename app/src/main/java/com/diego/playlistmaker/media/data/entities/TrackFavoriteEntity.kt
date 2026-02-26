package com.diego.playlistmaker.media.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_favorite_table")
data class TrackFavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val previewUrl: String
)