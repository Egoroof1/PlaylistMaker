package com.diego.playlistmaker.search.data.mapper

import com.diego.playlistmaker.media.data.entities.TrackFavoriteEntity
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

fun Track.toEntity(): TrackFavoriteEntity{
    return TrackFavoriteEntity(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        previewUrl = this.previewUrl
    )
}

fun TrackFavoriteEntity.toTrack(): Track{
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