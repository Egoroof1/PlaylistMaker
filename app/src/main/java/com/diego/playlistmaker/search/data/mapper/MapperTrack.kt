package com.diego.playlistmaker.search.data.mapper

import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.domain.models.Track

fun Track.toDto(): TrackDto{
    return TrackDto(
        this.trackId,
        this.trackName,
        this.artistName,
        this.collectionName,
        this.releaseDate,
        this.primaryGenreName,
        this.country,
        this.trackTimeMillis,
        this.artworkUrl100,
        this.previewUrl
    )
}

fun TrackDto.toTrack(): Track{
    return Track(
        this.trackId,
        this.trackName,
        this.artistName,
        this.collectionName,
        this.releaseDate,
        this.primaryGenreName,
        this.country,
        this.trackTimeMillis,
        this.artworkUrl100,
        this.previewUrl
    )
}