package com.diego.playlistmaker.media.domain.models

data class PlayList(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverImagePath: String = "",
    val quantityTracks: Int = 0,
    val totalTimeMillis: Long = 0
)
