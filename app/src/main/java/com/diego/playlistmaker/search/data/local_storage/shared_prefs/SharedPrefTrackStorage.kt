package com.diego.playlistmaker.search.data.local_storage.shared_prefs

import android.content.Context
import android.content.SharedPreferences
import com.diego.playlistmaker.search.data.models.TrackDto
import com.diego.playlistmaker.search.data.local_storage.TrackDtoStorage
import com.google.gson.Gson
import androidx.core.content.edit

class SharedPrefTrackStorage(context: Context) : TrackDtoStorage {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("history_settings", Context.MODE_PRIVATE)
    }

    override fun get(): List<TrackDto> {
        val json = sharedPrefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return try {
            gson.fromJson(json, Array<TrackDto>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun save(trackDto: TrackDto): Boolean {
        val currentList = get().toMutableList()
        currentList.add(trackDto)
        val json = gson.toJson(currentList)
        sharedPrefs.edit { putString(KEY_HISTORY, json) }
        return true
    }

    override fun clear(): Boolean {
        sharedPrefs.edit { putString(KEY_HISTORY, "[]") }
        return true
    }

    companion object {
        private const val KEY_HISTORY = "history"
        private val gson = Gson()
    }
}