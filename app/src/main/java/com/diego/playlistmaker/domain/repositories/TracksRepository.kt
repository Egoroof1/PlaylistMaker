package com.diego.playlistmaker.domain.repositories

import com.diego.playlistmaker.domain.entities.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}