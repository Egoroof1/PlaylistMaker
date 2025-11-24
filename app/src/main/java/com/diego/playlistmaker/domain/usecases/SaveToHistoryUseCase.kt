package com.diego.playlistmaker.domain.usecases

import com.diego.playlistmaker.domain.entities.Track
import com.diego.playlistmaker.domain.repositories.LocalHistoryRepository

class SaveToHistoryUseCase(
    private val historyRepository: LocalHistoryRepository
) {
    fun execute(track: Track, currentHistory: List<Track>) {
        val newHistory = mutableListOf<Track>().apply {

            if (currentHistory.contains(track)) {
                addAll(currentHistory.filter { it != track })
            } else {
                addAll(currentHistory)
            }
            add(0, track)
            if (size > 10){
                removeAt(lastIndex)
            }
        }
        historyRepository.saveHistory(newHistory)
    }
}