package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity

class TrackInPlayListRepositoryImpl(
    private val database: AppDatabase
) : TrackInPlayListRepository {
    override suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity) {
        database.trackInPlayListDao().insertTrackInPlayList(trackInPlayListEntity)
    }

    override suspend fun deleteTrackForPlayListById(trackId: Int) {
        database.trackInPlayListDao().deleteTrackForPlayListById(trackId)
    }
}