package com.diego.playlistmaker.player.models

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val trackInfo: TrackInfo? = null,
    val currentPosition: Int = 0
)
