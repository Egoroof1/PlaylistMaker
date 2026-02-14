package com.diego.playlistmaker.player.models

data class TrackInfo(
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val genre: String,
    val country: String,
    val releaseYear: String,
    val artworkUrl: String,
    val originalArtworkUrl: String,
    val trackTimeMillis: Long,
    val previewUrl: String
)
