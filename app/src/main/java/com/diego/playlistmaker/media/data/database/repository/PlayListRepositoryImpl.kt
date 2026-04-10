package com.diego.playlistmaker.media.data.database.repository

import com.diego.playlistmaker.media.data.database.dao.PlayListDao
import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class PlayListRepositoryImpl(
    private val playListDao: PlayListDao
) : PlayListRepository {
    override suspend fun insertPlayList(playListEntity: PlayListEntity) {
        playListDao.insertPlayList(playListEntity)
    }

    override suspend fun deletePlayListById(playListId: Int) {
        playListDao.deletePlayListById(playListId)
    }

    override suspend fun getPlayListById(playListId: Int): Flow<PlayListEntity?> {
        return playListDao.getPlayListById(playListId)
    }

    override fun getAllPlayList(): Flow<List<PlayListEntity>> {
        return playListDao.getAllPlayList().distinctUntilChanged()
    }

    override fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>> {
        return playListDao.getAllTracksForPlayList().distinctUntilChanged()
    }

    override suspend fun updatePlayList(playListId: Int, name: String, description: String, coverImagePath: String){
        playListDao.updatePlayList(playListId, name, description, coverImagePath)
    }

    override suspend fun incrementTracksCount(playListId: Int) {
        playListDao.incrementTracksCount(playListId)
    }

    override suspend fun decrementTracksCount(playListId: Int) {
        playListDao.decrementTracksCount(playListId)
    }

    override suspend fun addTotalTimeMillis(playListId: Int, timeMillis: Long) {
        playListDao.addTotalTimeMillis(playListId, timeMillis)
    }
}