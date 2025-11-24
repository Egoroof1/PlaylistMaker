package com.diego.playlistmaker.data.repositories

import com.diego.playlistmaker.data.remote.dto.TracksSearchRequest
import com.diego.playlistmaker.data.remote.dto.TracksSearchResponse
import com.diego.playlistmaker.data.remote.network.NetworkClient
import com.diego.playlistmaker.domain.repositories.TracksRepository
import com.diego.playlistmaker.domain.entities.Track


/**
 * Задача этой реализации — получить список фильмов, используя сетевой клиент,
 * и вернуть его в виде List<Track>
 */
class TrackRepositoryImpl (
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode) {
            200 -> {
                val searchResponse = response as? TracksSearchResponse
                if (searchResponse?.results?.isNotEmpty() == true) {
                    searchResponse.results.map { dto ->
                        Track(
                            dto.trackId, dto.trackName, dto.artistName,
                            dto.collectionName, dto.releaseDate, dto.primaryGenreName,
                            dto.country, dto.trackTimeMillis, dto.artworkUrl100,
                            dto.previewUrl
                        )
                    }
                } else {
                    // Пустой результат - это НЕ ошибка, а нормальный случай
                    emptyList()
                }
            }
            else -> {
                // Любой код кроме 200 - это ошибка
                throw Exception("HTTP_ERROR_${response.resultCode}")
            }
        }
    }
}