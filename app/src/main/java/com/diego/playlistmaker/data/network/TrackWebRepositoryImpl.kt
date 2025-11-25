package com.diego.playlistmaker.data.network

import android.util.Log
import com.diego.playlistmaker.domain.entity.SearchResult
import com.diego.playlistmaker.domain.repository.TrackWebRepository
import com.diego.playlistmaker.data.network.services.ITunesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackWebRepositoryImpl : TrackWebRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    override fun getTracksForWeb(query: String, callback: (SearchResult) -> Unit) {

        Log.d("TAG", "getTracksForWeb: Impl - real API call")

        iTunesApi.searchSongs(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val tracks = response.body()?.results ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        callback(SearchResult.Success(tracks))
                    } else {
                        callback(SearchResult.NotFound)
                    }
                    Log.d("TAG", "onResponse: found ${tracks.size} tracks")
                } else {
                    // Серверная ошибка (не 200)
                    callback(SearchResult.NetworkError)
                    Log.d("TAG", "onResponse: Server error ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                // Сетевая ошибка
                callback(SearchResult.NetworkError)
                Log.d("TAG", "onFailure: Network error $t")
            }
        })
    }

    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }
}