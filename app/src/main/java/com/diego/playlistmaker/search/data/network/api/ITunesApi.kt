package com.diego.playlistmaker.search.data.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchSongs(@Query("term") text: String): Response<TrackResponse>
}