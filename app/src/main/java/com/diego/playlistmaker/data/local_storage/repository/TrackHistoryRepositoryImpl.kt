package com.diego.playlistmaker.data.local_storage.repository

import com.diego.playlistmaker.data.models.TrackDto
import com.diego.playlistmaker.data.local_storage.TrackDtoStorage
import com.diego.playlistmaker.domain.searchActv.models.Track
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository

class TrackHistoryRepositoryImpl(
    private val trackDtoStorage: TrackDtoStorage
) : TrackHistoryRepository {

    override fun saveTrackToHistory(track: Track): Boolean {
        val trackDto = TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.previewUrl
        )

        return trackDtoStorage.save(trackDto)
    }

    override fun getTracksForHistory(): List<Track> {

        val tracks = trackDtoStorage.get().map { trackDto ->
            Track(
                trackDto.trackId,
                trackDto.trackName,
                trackDto.artistName,
                trackDto.collectionName,
                trackDto.releaseDate,
                trackDto.primaryGenreName,
                trackDto.country,
                trackDto.trackTimeMillis,
                trackDto.artworkUrl100,
                trackDto.previewUrl
            )
        }

        return tracks
     }

    override fun clearHistory(): Boolean {
        return trackDtoStorage.clear()
    }
}