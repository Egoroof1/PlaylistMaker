package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {

    suspend fun insertPlayList(playListEntity: PlayListEntity)

    suspend fun deletePlayListById(playListId: Int)

    suspend fun getPlayListById(playListId: Int): PlayListEntity

    fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>>
}