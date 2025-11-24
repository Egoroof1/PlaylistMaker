package com.diego.playlistmaker.domain.usecases

import com.diego.playlistmaker.data.repositories.TrackRepositoryImpl
import com.diego.playlistmaker.domain.entities.Track

class SearchTrackUseCase(
    private val repository: TrackRepositoryImpl
) {
    fun execute(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}