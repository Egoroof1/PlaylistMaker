package com.diego.playlistmaker.domain.use_case

import com.diego.playlistmaker.domain.entity.Track
import com.diego.playlistmaker.domain.repository.TrackLocalRepository

class GetTracksListLocalUseCase(
    private val trackLocalRepository: TrackLocalRepository
) {
    fun execute(): List<Track> {
        return trackLocalRepository.getTracksForLocal()
    }
}