package com.diego.playlistmaker.search.data.network.retrofit

import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.data.models.UserRequestParamDto
import com.diego.playlistmaker.search.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.search.data.network.api.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitTrackWeb : TrackDtoRetrofit {

    private val iTunesApi: ITunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

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

    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }
}