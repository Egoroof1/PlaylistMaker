package com.diego.playlistmaker

import android.content.Context
import com.diego.playlistmaker.data.local_storage.repository.TrackHistoryRepositoryImpl
import com.diego.playlistmaker.data.local_storage.shared_prefs.SharedPrefTrackStorage
import com.diego.playlistmaker.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.data.network.retrofit.RetrofitTrackWeb
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository
import com.diego.playlistmaker.domain.searchActv.repository.TrackWebRepository
import com.diego.playlistmaker.domain.searchActv.use_case.ClearTrackHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.GetTracksHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.SaveTrackHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.SearchTracksWebUseCase

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
    fun provideSearchTracksUseCase(): SearchTracksWebUseCase {
        return SearchTracksWebUseCase(provideTrackWebRepository())
    }

    // Репозитории
    private fun provideTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(provideTrackDtoStorage(context))
    }

    private fun provideTrackWebRepository(): TrackWebRepository {
        return TrackWebRepositoryImpl(provideTrackDtoRetrofit())
    }

    // Network
    private fun provideTrackDtoRetrofit(): TrackDtoRetrofit {
        return RetrofitTrackWeb()
    }

    // Storage
    private fun provideTrackDtoStorage(context: Context): SharedPrefTrackStorage {
        return SharedPrefTrackStorage(context)
    }
}