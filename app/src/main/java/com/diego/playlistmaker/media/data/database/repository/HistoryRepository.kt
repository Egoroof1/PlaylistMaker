package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun historyTracks(): Flow<List<TrackHistoryEntity>>

    suspend fun getHistoryTracks(): List<TrackHistoryEntity>

    suspend fun insertTrack(track: TrackHistoryEntity)

    suspend fun deleteAll()
    suspend fun deleteFirstTrack()

    suspend fun deleteById(trackId: Int)
}