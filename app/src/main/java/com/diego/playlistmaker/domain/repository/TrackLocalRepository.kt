package com.diego.playlistmaker.domain.repository

import com.diego.playlistmaker.domain.entity.Track

interface TrackLocalRepository {
    fun getTracksForLocal(): List<Track>
    fun saveTrackToHistory(track: Track)
    fun clearHistory()
}