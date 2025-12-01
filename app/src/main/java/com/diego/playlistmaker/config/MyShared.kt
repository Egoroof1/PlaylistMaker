package com.diego.playlistmaker.config

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object MyShared {
    private const val NAME_FILE_KEY = "settings"
    private const val KEY_THEME = "theme"
    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        // Используем applicationContext чтобы избежать утечек памяти
        sharedPrefs =
            context.applicationContext.getSharedPreferences(NAME_FILE_KEY, Context.MODE_PRIVATE)
        // Определяем системную тему
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(!sharedPrefs.contains(KEY_THEME)){
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                saveTheme(true)
            } else {
                saveTheme(false)
            }
        }
    }

    fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_THEME, isDarkTheme)
        }
    }

    fun getTheme(): Boolean = sharedPrefs.getBoolean(KEY_THEME, false)

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