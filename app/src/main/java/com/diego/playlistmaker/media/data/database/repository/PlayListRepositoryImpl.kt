package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

class PlayListRepositoryImpl(
    private val database: AppDatabase
) : PlayListRepository {
    override suspend fun insertPlayList(playListEntity: PlayListEntity) {
        database.playListDao().insertPlayList(playListEntity)
    }

    override suspend fun deletePlayListById(playListId: Int) {
        database.playListDao().deletePlayListById(playListId)
    }

    override suspend fun getPlayListById(playListId: Int): PlayListEntity? {
        return database.playListDao().getPlayListById(playListId)
    }

    override fun getAllPlayList(): Flow<List<PlayListEntity>> {
        return database.playListDao().getAllPlayList()
    }

    override fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>> {
        return database.playListDao().getAllTracksForPlayList()
    }

    override suspend fun updatePlayList(playListEntity: PlayListEntity) {
        database.playListDao().updatePlayList(playListEntity)
    }

    override suspend fun incrementTracksCount(playListId: Int) {
        database.playListDao().incrementTracksCount(playListId)
    }

    override suspend fun decrementTracksCount(playListId: Int) {
        database.playListDao().decrementTracksCount(playListId)
    }

    override suspend fun updateTotalTimeMillis(playListId: Int, timeMillis: Long) {
        database.playListDao().addTotalTimeMillis(playListId, timeMillis)
    }
}