package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

class SaveTrackHistoryUseCase(
    private val repository: TrackHistoryRepository
) {
    fun execute(track: Track): Boolean{

        val currentHistory = repository.getTracksForHistory().toMutableList()

        // Удаляем дубликаты
        currentHistory.removeAll { it.trackId == track.trackId }

        //Добав в начало
        currentHistory.add(0, track)

        // Ограничиваем размер
        if (currentHistory.size > 10){
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