package com.diego.playlistmaker.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.diego.playlistmaker.domain.repositories.SettingsThemeRepository

class SettingsThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : SettingsThemeRepository {

    override fun getTheme(): Boolean {
        // Если тема не сохранена, используем системную
        if (!sharedPreferences.contains(KEY_THEME)) {
            // сохраняем системную
            saveTheme(isSystemDarkTheme())
            return isSystemDarkTheme()
        }
        return sharedPreferences.getBoolean(KEY_THEME, false)
    }

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_THEME, isDarkTheme)
        }
    }

    override fun applyTheme() {
        val isDarkTheme = getTheme()
        val themeMode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    private fun isSystemDarkTheme(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        private const val KEY_THEME = "theme"
    }
}