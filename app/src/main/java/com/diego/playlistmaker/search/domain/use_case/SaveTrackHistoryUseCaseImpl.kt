package com.diego.playlistmaker.search.domain.use_case

import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.repository.TrackHistoryRepository

interface SaveTrackHistoryUseCase {
    fun execute(track: Track): Boolean
}

class SaveTrackHistoryUseCaseImpl(
    private val repository: TrackHistoryRepository
) : SaveTrackHistoryUseCase {
    override fun execute(track: Track): Boolean {

        val currentHistory = repository.getTracksForHistory().toMutableList()

        // Удаляем дубликаты
        currentHistory.removeAll { it.trackId == track.trackId }

        //Добав в начало
        currentHistory.add(0, track)

        // Ограничиваем размер
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        // Очищаем старую историю и сохраняем новую
        repository.clearHistory()
        currentHistory.forEach {
            repository.saveTrackToHistory(it)
        }

        return true
    }
}