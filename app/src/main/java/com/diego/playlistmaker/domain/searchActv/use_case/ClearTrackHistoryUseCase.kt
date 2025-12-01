package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

class ClearTrackHistoryUseCase(
    private val repository: TrackHistoryRepository
) {
    fun execute(): Boolean{
        return repository.clearHistory()
    }
}