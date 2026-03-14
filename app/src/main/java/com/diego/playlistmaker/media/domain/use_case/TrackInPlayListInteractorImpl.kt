package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.database.repository.TrackInPlayListRepository
import com.diego.playlistmaker.media.domain.mapper.toTrackInPlayList
import com.diego.playlistmaker.media.domain.mapper.toTrackInPlayListEntity
import com.diego.playlistmaker.media.domain.models.TrackInPlayList

class TrackInPlayListInteractorImpl(
    private val repository: TrackInPlayListRepository
) : TrackInPlayListInteractor {
    override suspend fun insertTrackInPlayList(trackInPlayList: TrackInPlayList) {
        repository.insertTrackInPlayList(trackInPlayList.toTrackInPlayListEntity())
    }

    override suspend fun deleteTrackForPlayListById(trackId: Int) {
        repository.deleteTrackForPlayListById(trackId)
    }

    override suspend fun isPlayList(trackId: Int): Boolean {
        return repository.isPlayList(trackId)
    }

    override suspend fun getTrackInPlayListByIdTrack(trackId: Int): TrackInPlayList? {
        return repository.getTrackInPlayListByIdTrack(trackId)?.toTrackInPlayList()
    }
}

interface TrackInPlayListInteractor {
    suspend fun insertTrackInPlayList(trackInPlayList: TrackInPlayList)
    suspend fun deleteTrackForPlayListById(trackId: Int)

    suspend fun isPlayList(trackId: Int): Boolean

    suspend fun getTrackInPlayListByIdTrack(trackId: Int): TrackInPlayList?
}