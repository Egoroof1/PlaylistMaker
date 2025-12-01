package com.diego.playlistmaker.domain.searchActv.repository

import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.models.UserRequestParam

interface TrackWebRepository {
    suspend fun searchTracks(query: UserRequestParam): List<Track>
}