package com.diego.playlistmaker.search.data.mapper

import com.diego.playlistmaker.media.data.database.entities.TrackFavoriteEntity
import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity
import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.domain.models.Track

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

fun Track.toFavoriteEntity(): TrackFavoriteEntity{
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

fun Track.toHistoryEntity(): TrackHistoryEntity {
    return TrackHistoryEntity(
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

fun TrackHistoryEntity.toTrack(): Track{
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