package com.diego.playlistmaker.domain.use_case

import com.diego.playlistmaker.domain.entity.Track
import com.diego.playlistmaker.domain.repository.TrackLocalRepository

class SaveTrackToHistoryUseCase(
    private val trackLocalRepository: TrackLocalRepository
) {
    fun execute(track: Track) {
        trackLocalRepository.saveTrackToHistory(track)
    }
}