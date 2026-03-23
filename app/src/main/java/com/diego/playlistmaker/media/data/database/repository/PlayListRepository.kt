package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {

    suspend fun insertPlayList(playListEntity: PlayListEntity)

    suspend fun deletePlayListById(playListId: Int)

    suspend fun getPlayListById(playListId: Int): PlayListEntity?

    fun getAllPlayList(): Flow<List<PlayListEntity>>

    fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>>

    suspend fun updatePlayList(playListEntity: PlayListEntity)

    suspend fun incrementTracksCount(playListId: Int)

    suspend fun addTotalTimeMillis(playListId: Int, timeMillis: Long)

    suspend fun decrementTracksCount(playListId: Int)
}