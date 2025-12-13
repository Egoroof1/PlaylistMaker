package com.diego.playlistmaker.data.network.repository

import com.diego.playlistmaker.data.mapper.toTrack
import com.diego.playlistmaker.data.models.UserRequestParamDto
import com.diego.playlistmaker.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.models.UserRequestParam
import com.diego.playlistmaker.domain.searchActv.repository.TrackWebRepository

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