package com.diego.playlistmaker.data.local.entities

data class TrackEntity(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val previewUrl: String
)