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

    override fun getPlayListById(playListId: Int): PlayListEntity {
        return database.playListDao().getPlayListById(playListId)
    }

    override fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>> {
        return database.playListDao().getAllTracksForPlayList()
    }
}