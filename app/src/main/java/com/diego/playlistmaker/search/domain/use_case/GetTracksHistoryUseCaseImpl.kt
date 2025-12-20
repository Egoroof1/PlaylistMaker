package com.diego.playlistmaker.search.domain.use_case

import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.repository.TrackHistoryRepository

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