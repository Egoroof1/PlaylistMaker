package com.diego.playlistmaker.data.network

import com.diego.playlistmaker.data.models.TrackDto
import com.diego.playlistmaker.data.models.UserRequestParamDto

interface TrackDtoRetrofit {
    suspend fun search(query: UserRequestParamDto): Result<List<TrackDto>>
}