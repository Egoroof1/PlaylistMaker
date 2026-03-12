package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.database.repository.PlayListRepository
import com.diego.playlistmaker.media.domain.mapper.toPlayList
import com.diego.playlistmaker.media.domain.mapper.toPlayListEntity
import com.diego.playlistmaker.media.domain.mapper.toTrackInPlayList
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.models.TrackInPlayList
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

    override suspend fun getPlayListById(playListId: Int): PlayList {
        return repository.getPlayListById(playListId).toPlayList()
    }

    override fun getAllTracksForPlayList(): Flow<List<TrackInPlayList>> {
        return repository.getAllTracksForPlayList().map { entities ->
            entities.map {
                it.toTrackInPlayList()
            }
        }
    }
}

interface PlayListInteractor {
    suspend fun insertPlayList(playList: PlayList)

    suspend fun deletePlayListById(playListId: Int)

    suspend fun getPlayListById(playListId: Int): PlayList

    fun getAllTracksForPlayList(): Flow<List<TrackInPlayList>>
}