package com.diego.playlistmaker.domain.searchActv.use_case

import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.models.UserRequestParam
import com.diego.playlistmaker.domain.searchActv.repository.TrackWebRepository

class SearchTracksWebUseCase(
    private val repository: TrackWebRepository
) {
    suspend fun execute(query: UserRequestParam): List<Track> {
        return repository.searchTracks(query)
    }
}