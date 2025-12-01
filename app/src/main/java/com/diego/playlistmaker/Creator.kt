package com.diego.playlistmaker.creator

import android.content.Context
import com.diego.playlistmaker.data.local_storage.repository.TrackHistoryRepositoryImpl
import com.diego.playlistmaker.data.local_storage.shared_prefs.SharedPrefTrackStorage
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository
import com.diego.playlistmaker.domain.searchActv.repository.TrackWebRepository
import com.diego.playlistmaker.domain.searchActv.use_case.ClearTrackHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.GetTracksHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.SaveTrackHistoryUseCase

object Creator {

    // Use Cases для истории треков
    fun provideGetTracksHistoryUseCase(context: Context): GetTracksHistoryUseCase {
        return GetTracksHistoryUseCase(provideTrackHistoryRepository(context))
    }

    fun provideSaveTrackHistoryUseCase(context: Context): SaveTrackHistoryUseCase {
        return SaveTrackHistoryUseCase(provideTrackHistoryRepository(context))
    }

    fun provideClearTrackHistoryUseCase(context: Context): ClearTrackHistoryUseCase {
        return ClearTrackHistoryUseCase(provideTrackHistoryRepository(context))
    }

    // Use Cases для веб-поиска (заглушка)
    fun provideSearchTracksUseCase(): TrackWebRepository {
        return provideTrackWebRepository()
    }

    // Репозитории
    private fun provideTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(provideTrackDtoStorage(context))
    }

    private fun provideTrackWebRepository(): TrackWebRepository {
        // TODO: Реализовать когда будет сетевой слой
        throw NotImplementedError("TrackWebRepository not implemented yet")
    }

    // Storage
    private fun provideTrackDtoStorage(context: Context): SharedPrefTrackStorage {
        return SharedPrefTrackStorage(context)
    }
}