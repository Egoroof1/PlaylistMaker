package com.diego.playlistmaker.search.data.network

import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.data.models.UserRequestParamDto

interface TrackDtoRetrofit {
    suspend fun search(query: UserRequestParamDto): Result<List<TrackDto>>
}