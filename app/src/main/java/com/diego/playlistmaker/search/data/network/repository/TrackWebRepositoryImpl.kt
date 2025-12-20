package com.diego.playlistmaker.search.data.network.repository

import com.diego.playlistmaker.search.data.mapper.toTrack
import com.diego.playlistmaker.search.data.models.UserRequestParamDto
import com.diego.playlistmaker.search.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.models.UserRequestParam
import com.diego.playlistmaker.search.domain.repository.TrackWebRepository

class TrackWebRepositoryImpl(
    private val trackDtoRetrofit: TrackDtoRetrofit
) : TrackWebRepository {
    override suspend fun searchTracks(query: UserRequestParam): List<Track> {
        val paramDto = UserRequestParamDto(query.param)
        val result = trackDtoRetrofit.search(paramDto)

        return result.getOrThrow().map { trackDto ->
            trackDto.toTrack()
        }
    }
}