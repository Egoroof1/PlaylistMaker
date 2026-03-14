package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity

interface TrackInPlayListRepository {
    suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity)
    suspend fun deleteTrackForPlayListById(trackId: Int)
    suspend fun isPlayList(trackId: Int): Boolean
    suspend fun getTrackInPlayListByIdTrack(trackId: Int): TrackInPlayListEntity?
}