package com.diego.playlistmaker.player.models

import com.diego.playlistmaker.media.domain.models.PlayList

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val trackInfo: TrackInfo? = null,
    val currentPosition: Int = 0,
    val isLike: Boolean = false,
    val isPlayList: Boolean = false,
    val playListId: Int = 0,
    val playListList: List<PlayList> = emptyList()
)
