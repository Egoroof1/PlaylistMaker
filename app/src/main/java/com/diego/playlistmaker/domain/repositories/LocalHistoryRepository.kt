package com.diego.playlistmaker.domain.repositories

import com.diego.playlistmaker.domain.entities.Track

interface LocalHistoryRepository {
    fun getHistory(): List<Track>
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}