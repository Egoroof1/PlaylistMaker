package com.diego.playlistmaker.domain.searchActv.repository

import com.diego.playlistmaker.domain.searchActv.models.Track
import com.diego.playlistmaker.domain.searchActv.models.UserRequestParam

interface TrackWebRepository {
    fun searchTracks(query: UserRequestParam): List<Track>
}