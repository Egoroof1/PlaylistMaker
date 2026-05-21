package com.diego.playlistmaker.services

import com.diego.playlistmaker.player.models.PlayerScreenState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerScreenState>
    fun startPlayer()
    fun pausePlayer()
}