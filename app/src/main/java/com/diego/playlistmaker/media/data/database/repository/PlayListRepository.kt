package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {

    suspend fun insertPlayList(playListEntity: PlayListEntity)

    suspend fun deletePlayListById(playListId: Int)

    suspend fun getPlayListById(playListId: Int): Flow<PlayListEntity?>

    fun getAllPlayList(): Flow<List<PlayListEntity>>

    fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>>

    suspend fun updatePlayList(playListId: Int, name: String, description: String, coverImagePath: String)

    suspend fun incrementTracksCount(playListId: Int)

    suspend fun addTotalTimeMillis(playListId: Int, timeMillis: Long)

    suspend fun decrementTracksCount(playListId: Int)
}