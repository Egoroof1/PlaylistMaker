package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.repository.HistoryRepository
import com.diego.playlistmaker.search.data.mapper.toHistoryEntity
import com.diego.playlistmaker.search.data.mapper.toTrack
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryInteractor(
    private val repository: HistoryRepository
) : HistoryRepositoryUseCase {
    override fun historyTracks(): Flow<List<Track>> {
        return repository.historyTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun getHistoryTracks(): List<Track> {
        return repository.getHistoryTracks().map { it.toTrack() }
    }

    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track.toHistoryEntity())
    }

    override suspend fun deleteAll() {
        repository.deleteAll()
    }

    override suspend fun deleteFirstTrack() {
        repository.deleteFirstTrack()
    }

    override suspend fun deleteById(trackId: Int) {
        repository.deleteById(trackId)
    }
}

interface HistoryRepositoryUseCase{
    fun historyTracks(): Flow<List<Track>>
    suspend fun getHistoryTracks(): List<Track>
    suspend fun insertTrack(track: Track)
    suspend fun deleteAll()
    suspend fun deleteFirstTrack()
    suspend fun deleteById(trackId: Int)
}