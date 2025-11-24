package com.diego.playlistmaker.domain.usecases

import com.diego.playlistmaker.domain.entities.Track
import com.diego.playlistmaker.domain.repositories.LocalHistoryRepository

class GetHistoryUseCase(
    private val historyRepository: LocalHistoryRepository
) {
    fun execute(): List<Track> = historyRepository.getHistory()
}