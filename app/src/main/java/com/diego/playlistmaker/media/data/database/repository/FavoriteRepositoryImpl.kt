package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.dao.TrackFavoriteDao
import com.diego.playlistmaker.media.data.database.entities.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class FavoriteRepositoryImpl(
    private val trackFavoriteDao: TrackFavoriteDao
) : FavoriteRepository {
    override fun favoriteTracks(): Flow<List<TrackFavoriteEntity>> {
        return trackFavoriteDao.getTracks().distinctUntilChanged()
    }

    override suspend fun isFavorite(trackId: Int): Boolean =
        trackFavoriteDao.isFavorite(trackId)

    override suspend fun insertTrack(track: TrackFavoriteEntity) {
        trackFavoriteDao.insertTrack(track)
    }

    override suspend fun deleteById(trackId: Int) {
        trackFavoriteDao.deleteById(trackId)
    }
}