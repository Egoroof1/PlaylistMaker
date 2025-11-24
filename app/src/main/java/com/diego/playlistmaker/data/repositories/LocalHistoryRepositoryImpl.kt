package com.diego.playlistmaker.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.diego.playlistmaker.data.local.entities.TrackEntity
import com.diego.playlistmaker.domain.entities.Track
import com.diego.playlistmaker.domain.repositories.LocalHistoryRepository
import com.google.gson.Gson


class LocalHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : LocalHistoryRepository{
    private val KEY_HISTORY = "history"

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, "[]")
        return if(json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val tracks = Gson().fromJson(json, Array<TrackEntity>::class.java).toList()
                return tracks.map { it.toTrack() }
            } catch (e: Exception){
                emptyList()
            }
        }
    }

    override fun saveHistory(history: List<Track>) {
        val tracks = history.map { track ->
            TrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                artworkUrl100 = track.artworkUrl100,
                previewUrl = track.previewUrl
            )
        }
        val json = Gson().toJson(tracks)
        sharedPreferences.edit { putString(KEY_HISTORY, json) }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(KEY_HISTORY) }
    }
}

private fun TrackEntity.toTrack(): Track = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    collectionName = "",
    releaseDate = "",
    primaryGenreName = "",
    country = "",
    trackTimeMillis = 0,
    artworkUrl100 = artworkUrl100,
    previewUrl = previewUrl
)