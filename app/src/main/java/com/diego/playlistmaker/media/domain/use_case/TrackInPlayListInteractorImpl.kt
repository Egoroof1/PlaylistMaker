package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.database.repository.TrackInPlayListRepository
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
}

interface TrackInPlayListInteractor {
    suspend fun insertTrackInPlayList(trackInPlayList: TrackInPlayList)
    suspend fun deleteTrackForPlayListById(trackId: Int)
}