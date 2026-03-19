package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

class TrackInPlayListRepositoryImpl(
    private val database: AppDatabase
) : TrackInPlayListRepository {
    override suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity) {
        database.trackInPlayListDao().insertTrackInPlayList(trackInPlayListEntity)
    }

    override suspend fun deleteTrackForPlayListById(trackId: Int) {
        database.trackInPlayListDao().deleteTrackForPlayListById(trackId)
    }

    override suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayListEntity? {
        return database.trackInPlayListDao().getTrackInPlayListByTrackId(trackId)
    }

    override fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<TrackInPlayListEntity>> {
        return database.trackInPlayListDao().getAllTracksInPlayListByIdPlaylist(playListId)
    }
}