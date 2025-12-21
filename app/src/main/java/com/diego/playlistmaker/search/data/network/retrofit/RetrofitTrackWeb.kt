package com.diego.playlistmaker.search.data.network.retrofit

import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.data.models.UserRequestParamDto
import com.diego.playlistmaker.search.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.search.data.network.api.ITunesApi

class RetrofitTrackWeb(
    private val iTunesApi: ITunesApi
) : TrackDtoRetrofit {

    override suspend fun search(query: UserRequestParamDto): Result<List<TrackDto>> {
        return try {
            val response = iTunesApi.searchSongs(query.param)
            if (response.isSuccessful) {
                val tracks = response.body()?.results ?: emptyList()
                Result.success(tracks)
            } else {
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}