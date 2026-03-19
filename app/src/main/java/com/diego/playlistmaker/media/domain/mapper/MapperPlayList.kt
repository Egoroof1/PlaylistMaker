package com.diego.playlistmaker.media.domain.mapper

import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.models.TrackInPlayList
import com.diego.playlistmaker.search.domain.models.Track

fun PlayListEntity.toPlayList(): PlayList {
    return PlayList(
        this.id,
        this.name,
        this.description,
        this.coverImagePath,
        this.quantityTracks,
        this.totalTimeMillis
    )
}

fun PlayList.toPlayListEntity(): PlayListEntity {
    return PlayListEntity(
        name = this.name,
        description = this.description,
        coverImagePath = this.coverImagePath,
        quantityTracks = this.quantityTracks,
        totalTimeMillis = this.totalTimeMillis
    )
}

fun TrackInPlayListEntity.toTrackInPlayList(): TrackInPlayList {
    return TrackInPlayList(
        Track(
            trackId,
            trackName,
            artistName,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            trackTimeMillis,
            artworkUrl100,
            previewUrl
        ),
        playlistId
    )
}

fun TrackInPlayListEntity.toTrack(): Track {
    return Track(
        trackId,
        trackName,
        artistName,
        collectionName,
        releaseDate,
        primaryGenreName,
        country,
        trackTimeMillis,
        artworkUrl100,
        previewUrl
    )
}

fun TrackInPlayList.toTrackInPlayListEntity(): TrackInPlayListEntity {
    return TrackInPlayListEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        trackTimeMillis = track.trackTimeMillis,
        artworkUrl100 = track.artworkUrl100,
        previewUrl = track.previewUrl,
        playlistId = playlistId
    )
}