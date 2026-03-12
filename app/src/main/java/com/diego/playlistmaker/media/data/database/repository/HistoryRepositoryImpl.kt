package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase
) : HistoryRepository {
    override fun historyTracks(): Flow<List<TrackHistoryEntity>> {
        return appDatabase.trackHistoryDao().getTracks()
    }

    override suspend fun getHistoryTracks(): List<TrackHistoryEntity> {
        return appDatabase.trackHistoryDao().getHistoryTracks()
    }

    override suspend fun insertTrack(track: TrackHistoryEntity) {
        appDatabase.trackHistoryDao().insertTrack(track)
    }

    override suspend fun deleteAll() {
        appDatabase.trackHistoryDao().deleteAll()
    }

    override suspend fun deleteFirstTrack() {
        appDatabase.trackHistoryDao().deleteFirstTrack()
    }

    override suspend fun deleteById(trackId: Int) {
        appDatabase.trackHistoryDao().deleteById(trackId)
    }
}