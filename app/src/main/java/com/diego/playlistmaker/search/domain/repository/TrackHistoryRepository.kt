package com.diego.playlistmaker.search.domain.repository

import com.diego.playlistmaker.search.domain.models.Track

interface TrackHistoryRepository {

    fun saveTrackToHistory(track: Track): Boolean
    fun getTracksForHistory(): List<Track>
    fun clearHistory(): Boolean
}