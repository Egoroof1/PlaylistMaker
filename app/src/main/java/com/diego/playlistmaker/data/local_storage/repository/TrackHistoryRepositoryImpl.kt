package com.diego.playlistmaker.data.local_storage.repository

import com.diego.playlistmaker.data.local_storage.TrackDtoStorage
import com.diego.playlistmaker.data.mapper.toDto
import com.diego.playlistmaker.data.mapper.toTrack
import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

class TrackHistoryRepositoryImpl(
    private val trackDtoStorage: TrackDtoStorage
) : TrackHistoryRepository {

    override fun saveTrackToHistory(track: Track): Boolean {

        return trackDtoStorage.save(track.toDto())
    }

    override fun getTracksForHistory(): List<Track> {

        val tracks = trackDtoStorage.get().map { trackDto ->
            trackDto.toTrack()
        }

        return tracks
     }

    override fun clearHistory(): Boolean {
        return trackDtoStorage.clear()
    }
}