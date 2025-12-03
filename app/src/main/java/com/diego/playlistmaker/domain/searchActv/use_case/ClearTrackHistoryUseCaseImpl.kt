package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

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