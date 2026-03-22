package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.dao.TrackInPlayListDao
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class TrackInPlayListRepositoryImpl(
    private val trackInPlayListDao: TrackInPlayListDao
) : TrackInPlayListRepository {
    override suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity) {
        trackInPlayListDao.insertTrackInPlayList(trackInPlayListEntity)
    }

    override suspend fun deleteTrackForPlayListById(trackId: Int) {
        trackInPlayListDao.deleteTrackForPlayListById(trackId)
    }

    override suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayListEntity? {
        return trackInPlayListDao.getTrackInPlayListByTrackId(trackId)
    }

    override fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<TrackInPlayListEntity>> {
        return trackInPlayListDao.getAllTracksInPlayListByIdPlaylist(playListId).distinctUntilChanged()
    }
}