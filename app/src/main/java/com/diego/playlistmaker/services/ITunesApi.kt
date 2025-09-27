package com.diego.playlistmaker.services

import com.diego.playlistmaker.models.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term") text: String): Call<TrackResponse>
}