package com.diego.playlistmaker.search.domain.use_case

import com.diego.playlistmaker.search.domain.repository.TrackHistoryRepository

interface ClearTrackHistoryUseCase {
    fun execute(): Boolean
}

class ClearTrackHistoryUseCaseImpl(
    private val repository: TrackHistoryRepository
) : ClearTrackHistoryUseCase {
    override fun execute(): Boolean {
        return repository.clearHistory()
    }
}