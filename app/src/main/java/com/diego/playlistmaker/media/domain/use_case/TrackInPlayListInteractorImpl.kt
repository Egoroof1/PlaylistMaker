package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.database.repository.TrackInPlayListRepository
import com.diego.playlistmaker.media.domain.mapper.toTrack
import com.diego.playlistmaker.media.domain.mapper.toTrackInPlayList
import com.diego.playlistmaker.media.domain.mapper.toTrackInPlayListEntity
import com.diego.playlistmaker.media.domain.models.TrackInPlayList
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInPlayListInteractorImpl(
    private val repository: TrackInPlayListRepository
) : TrackInPlayListInteractor {
    override suspend fun insertTrackInPlayList(trackInPlayList: TrackInPlayList) {
        repository.insertTrackInPlayList(trackInPlayList.toTrackInPlayListEntity())
    }

    override suspend fun deleteTrackForPlayListById(trackId: Int, playListId: Int) {
        repository.deleteTrackForPlayListById(trackId, playListId)
    }

    override suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayList? {
        return repository.getTrackInPlayListByTrackId(trackId)?.toTrackInPlayList()
    }

    override fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<Track>> {
        return repository.getAllTracksInPlayListByIdPlaylist(playListId).map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun deleteAllTrackFromPlaylist(playListId: Int) {
        repository.deleteAllTrackFromPlaylist(playListId)
    }
}

interface TrackInPlayListInteractor {
    suspend fun insertTrackInPlayList(trackInPlayList: TrackInPlayList)
    suspend fun deleteTrackForPlayListById(trackId: Int, playListId: Int)
    suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayList?
    fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<Track>>
    suspend fun deleteAllTrackFromPlaylist(playListId: Int)
}