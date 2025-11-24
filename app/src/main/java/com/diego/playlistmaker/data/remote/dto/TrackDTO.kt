package com.diego.playlistmaker.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель для данных из сети (API)
 * Отличается от domain Track - здесь могут быть другие названия полей,
 * форматы дат, или дополнительные/отсутствующие поля
 */
data class TrackDTO(
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("collectionName") val collectionName: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("country") val country: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("previewUrl") val previewUrl: String
)