package com.diego.playlistmaker.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.diego.playlistmaker.models.Track
import com.google.gson.Gson

object MyShared {
    private const val NAME_FILE_KEY = "settings"
    private const val KEY_THEME = "theme"
    private const val KEY_HISTORY = "history"

    private const val KEY_CURRENT_TRACK = "current_track"
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        // Используем applicationContext чтобы избежать утечек памяти
        sharedPrefs =
            context.applicationContext.getSharedPreferences(NAME_FILE_KEY, Context.MODE_PRIVATE)
        // Определяем системную тему
        val nightModeFlags =
            context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        if(!sharedPrefs.contains(KEY_THEME)){
            if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                saveTheme(true)
            } else {
                saveTheme(false)
            }
        }
    }

    fun saveCurrentTrack(track: Track) {
        val json = gson.toJson(track)
        sharedPrefs.edit {
            putString(KEY_CURRENT_TRACK, json)
        }
    }

    fun getCurrentTrack(): Track? {
        val json = sharedPrefs.getString(KEY_CURRENT_TRACK, "")
        return if (json.isNullOrEmpty()) {
            null
        } else {
            try {
                gson.fromJson(json, Track::class.java)
            } catch (e: Exception) {
                Log.d("TAG", "getCurrentTrack: ERROR $e")
                null
            }
        }
    }

    fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_THEME, isDarkTheme)
        }
    }

    fun getTheme(): Boolean = sharedPrefs.getBoolean(KEY_THEME, false)

    fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit {
            putString(KEY_HISTORY, json)
        }
    }

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, "[]")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                Gson().fromJson(json, Array<Track>::class.java).toList()
            } catch (e: Exception) {
                Log.d("TAG", "getHistory: ERROR $e")
                emptyList()
            }
        }
    }

    fun clearHistory() {
        sharedPrefs.edit {
            remove(KEY_HISTORY)
        }
    }

    fun applyTheme() {
        val isDarkTheme = getTheme()
        val themeMode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}