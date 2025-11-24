package com.diego.playlistmaker.data.remote

import com.diego.playlistmaker.data.remote.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term") text: String): Call<TracksSearchResponse>
}