package com.diego.playlistmaker.media.data.repository

import com.diego.playlistmaker.media.data.entities.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun favoriteTracks(): Flow<List<TrackFavoriteEntity>>

    suspend fun isFavorite(trackId: Int): Boolean

    suspend fun insertTrack(track: TrackFavoriteEntity)

    suspend fun deleteById(trackId: Int)
}