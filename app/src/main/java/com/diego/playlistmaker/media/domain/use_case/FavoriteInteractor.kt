package com.diego.playlistmaker.media.domain.use_case

import com.diego.playlistmaker.media.data.repository.FavoriteRepository
import com.diego.playlistmaker.search.data.mapper.toFavoriteEntity
import com.diego.playlistmaker.search.data.mapper.toTrack
import com.diego.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteInteractor(
    private val favoriteRepository: FavoriteRepository
) : FavoriteRepositoryUseCase {
    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.favoriteTracks().
                map { entities ->
                    entities.map { it.toTrack() }
                }
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoriteRepository.isFavorite(trackId)
    }

    override suspend fun insertTrack(track: Track) {
        favoriteRepository.insertTrack(track.toFavoriteEntity())
    }

    override suspend fun deleteById(trackId: Int) {
        favoriteRepository.deleteById(trackId)
    }
}

interface FavoriteRepositoryUseCase {
    fun favoriteTracks(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Int): Boolean
    suspend fun insertTrack(track: Track)
    suspend fun deleteById(trackId: Int)
}