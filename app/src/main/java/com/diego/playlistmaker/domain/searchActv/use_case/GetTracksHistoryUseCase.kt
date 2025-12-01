package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.searchActv.models.Track
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

class GetTracksHistoryUseCase (
    private val repository: TrackHistoryRepository
) {
    fun execute(): List<Track>{
        return repository.getTracksForHistory()
    }
}