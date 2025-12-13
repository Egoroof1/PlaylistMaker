package com.diego.playlistmaker.domain.searchActv.repository

import com.diego.playlistmaker.domain.models.Track

interface TrackHistoryRepository {

    fun saveTrackToHistory(track: Track): Boolean
    fun getTracksForHistory(): List<Track>
    fun clearHistory(): Boolean
}