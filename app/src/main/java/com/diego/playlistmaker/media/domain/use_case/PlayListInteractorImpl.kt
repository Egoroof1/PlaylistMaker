package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.database.repository.PlayListRepository
import com.diego.playlistmaker.media.domain.mapper.toPlayList
import com.diego.playlistmaker.media.domain.mapper.toPlayListEntity
import com.diego.playlistmaker.media.domain.models.PlayList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayListInteractorImpl(
    private val repository: PlayListRepository
) : PlayListInteractor {
    override suspend fun insertPlayList(playList: PlayList) {
        repository.insertPlayList(playList.toPlayListEntity())
    }

    override suspend fun deletePlayListById(playListId: Int) {
        repository.deletePlayListById(playListId)
    }

    override suspend fun getPlayListById(playListId: Int): Flow<PlayList?> {
        return repository.getPlayListById(playListId).map { playListEntity ->
            playListEntity?.toPlayList()
        }
    }

    override fun getAllPlayList(): Flow<List<PlayList>> {
        return repository.getAllPlayList().map { entities ->
            entities.map {
                it.toPlayList()
            }
        }
    }

    override suspend fun updatePlayList(playListId: Int, name: String, description: String, coverImagePath: String) {
        repository.updatePlayList(playListId, name, description, coverImagePath)
    }

    override suspend fun incrementTracksCount(playListId: Int) {
        repository.incrementTracksCount(playListId)
    }

    override suspend fun decrementTracksCount(playListId: Int) {
        repository.decrementTracksCount(playListId)
    }

    override suspend fun addTotalTimeMillis(playListId: Int, timeMillis: Long) {
        repository.addTotalTimeMillis(playListId, timeMillis)
    }
}

interface PlayListInteractor {
    suspend fun insertPlayList(playList: PlayList)

    suspend fun deletePlayListById(playListId: Int)

    suspend fun getPlayListById(playListId: Int): Flow<PlayList?>

    fun getAllPlayList(): Flow<List<PlayList>>

    suspend fun updatePlayList(playListId: Int, name: String, description: String, coverImagePath: String)

    suspend fun incrementTracksCount(playListId: Int)

    suspend fun decrementTracksCount(playListId: Int)

    suspend fun addTotalTimeMillis(playListId: Int, timeMillis: Long)
}