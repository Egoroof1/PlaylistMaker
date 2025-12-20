package com.diego.playlistmaker.search.domain.repository

import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.models.UserRequestParam

interface TrackWebRepository {
    suspend fun searchTracks(query: UserRequestParam): List<Track>
}