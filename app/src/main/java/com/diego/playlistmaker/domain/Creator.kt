package com.diego.playlistmaker.domain

import android.content.Context
import com.diego.playlistmaker.data.repositories.LocalHistoryRepositoryImpl
import com.diego.playlistmaker.data.repositories.SettingsThemeRepositoryImpl
import com.diego.playlistmaker.data.repositories.TrackRepositoryImpl
import com.diego.playlistmaker.domain.api.TracksInteractor
import com.diego.playlistmaker.domain.repositories.TracksRepository
import com.diego.playlistmaker.domain.impl.TracksInteractorImpl
import com.diego.playlistmaker.domain.repositories.LocalHistoryRepository
import com.diego.playlistmaker.domain.repositories.SettingsThemeRepository
import com.diego.playlistmaker.domain.usecases.ApplyThemeUseCase
import com.diego.playlistmaker.domain.usecases.ClearHistoryUseCase
import com.diego.playlistmaker.domain.usecases.GetHistoryUseCase
import com.diego.playlistmaker.domain.usecases.SearchTrackUseCase
import com.diego.playlistmaker.domain.usecases.GetThemeUseCase
import com.diego.playlistmaker.domain.usecases.SaveThemeUseCase
import com.diego.playlistmaker.domain.usecases.SaveToHistoryUseCase

object Creator {
    private const val NAME_FILE_KEY = "settings"

//    private fun

    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSettingsRepository(context: Context): SettingsThemeRepository {
        val sharedPrefs = context.getSharedPreferences(NAME_FILE_KEY, Context.MODE_PRIVATE)
        return SettingsThemeRepositoryImpl(sharedPrefs, context)
    }

    private fun getHistoryRepository(context: Context): LocalHistoryRepository {
        val sharedPrefs = context.getSharedPreferences(NAME_FILE_KEY, Context.MODE_PRIVATE)
        return LocalHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase {
        return GetHistoryUseCase(getHistoryRepository(context))
    }

    fun provideClearHistoryUseCase(context: Context): ClearHistoryUseCase {
        return ClearHistoryUseCase(getHistoryRepository(context))
    }

    fun provideSaveToHistoryUseCase(context: Context): SaveToHistoryUseCase {
        return SaveToHistoryUseCase(getHistoryRepository(context))
    }

    fun provideGetThemeUseCase(context: Context): GetThemeUseCase {
        return GetThemeUseCase(getSettingsRepository(context))
    }

    fun provideApplyThemeUseCase(context: Context): ApplyThemeUseCase {
        return ApplyThemeUseCase(getSettingsRepository(context))
    }

    fun provideSaveThemeUseCase(context: Context): SaveThemeUseCase {
        return SaveThemeUseCase(getSettingsRepository(context))
    }
}