package com.diego.playlistmaker.media.data.repository

import com.diego.playlistmaker.media.data.AppDatabase
import com.diego.playlistmaker.media.data.entities.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteRepository {
    override fun favoriteTracks(): Flow<List<TrackFavoriteEntity>> {
        return appDatabase.trackDao().getTracks()
    }

    override suspend fun isFavorite(trackId: Int): Boolean =
        appDatabase.trackDao().isFavorite(trackId)

    override suspend fun insertTrack(track: TrackFavoriteEntity) {
        appDatabase.trackDao().insertTrack(track)
    }

    override suspend fun deleteById(trackId: Int) {
        appDatabase.trackDao().deleteById(trackId)
    }
}