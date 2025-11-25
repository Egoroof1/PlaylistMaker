package com.diego.playlistmaker.domain.use_case

import com.diego.playlistmaker.domain.repository.TrackLocalRepository

class ClearHistoryUseCase(
    private val trackLocalRepository: TrackLocalRepository
) {
    fun execute() {
        trackLocalRepository.clearHistory()
    }
}