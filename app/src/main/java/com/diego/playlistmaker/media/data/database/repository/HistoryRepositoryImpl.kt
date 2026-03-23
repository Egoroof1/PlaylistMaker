package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.dao.TrackHistoryDao
import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class HistoryRepositoryImpl(
    private val trackHistoryDao: TrackHistoryDao
) : HistoryRepository {
    override fun historyTracks(): Flow<List<TrackHistoryEntity>> {
        return trackHistoryDao.getTracks().distinctUntilChanged()
    }

    override suspend fun getHistoryTracks(): List<TrackHistoryEntity> {
        return trackHistoryDao.getHistoryTracks()
    }

    override suspend fun insertTrack(track: TrackHistoryEntity) {
        trackHistoryDao.insertTrack(track)
    }

    override suspend fun deleteAll() {
        trackHistoryDao.deleteAll()
    }

    override suspend fun deleteFirstTrack() {
        trackHistoryDao.deleteFirstTrack()
    }

    override suspend fun deleteById(trackId: Int) {
        trackHistoryDao.deleteById(trackId)
    }
}