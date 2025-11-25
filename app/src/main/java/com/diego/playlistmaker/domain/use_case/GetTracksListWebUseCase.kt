package com.diego.playlistmaker.domain.use_case

import com.diego.playlistmaker.domain.entity.SearchResult
import com.diego.playlistmaker.domain.repository.TrackWebRepository

class GetTracksListWebUseCase(
    private val trackWebRepository: TrackWebRepository
) {
    fun execute(query: String, callback: (SearchResult) -> Unit) {
        trackWebRepository.getTracksForWeb(query, callback)
    }
}