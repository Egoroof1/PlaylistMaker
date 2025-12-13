package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

interface GetTracksHistoryUseCase {
    fun execute(): List<Track>
}

class GetTracksHistoryUseCaseImpl(
    private val repository: TrackHistoryRepository
) : GetTracksHistoryUseCase {
    override fun execute(): List<Track> {
        return repository.getTracksForHistory()
    }
}