package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

interface TrackInPlayListRepository {
    suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity)
    suspend fun deleteTrackForPlayListById(trackId: Int, playListId: Int)
    suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayListEntity?
    fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<TrackInPlayListEntity>>
}