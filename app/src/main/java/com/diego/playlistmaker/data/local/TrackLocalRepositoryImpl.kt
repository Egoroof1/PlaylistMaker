package com.diego.playlistmaker.data.local

import com.diego.playlistmaker.domain.entity.Track
import com.diego.playlistmaker.domain.repository.TrackLocalRepository

class TrackLocalRepositoryImpl : TrackLocalRepository {

    override fun getTracksForLocal(): List<Track> {
        return MyShared.getHistory()
    }

    override fun saveTrackToHistory(track: Track) {
        val currentHistory = MyShared.getHistory().toMutableList()

        // Удаляем трек если уже есть (чтобы не было дубликатов)
        currentHistory.removeAll { it.trackId == track.trackId }

        // Добавляем в начало
        currentHistory.add(0, track)

        // Ограничиваем размер истории
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        MyShared.saveHistory(currentHistory)
    }

    override fun clearHistory() {
        MyShared.clearHistory()
    }
}