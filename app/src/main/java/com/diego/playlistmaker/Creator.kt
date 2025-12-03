package com.diego.playlistmaker

import android.content.Context
import com.diego.playlistmaker.data.local_storage.repository.TrackHistoryRepositoryImpl
import com.diego.playlistmaker.data.local_storage.shared_prefs.SharedPrefTrackStorage
import com.diego.playlistmaker.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.data.network.retrofit.RetrofitTrackWeb
import com.diego.playlistmaker.domain.searchActv.repository.TrackHistoryRepository
import com.diego.playlistmaker.domain.searchActv.repository.TrackWebRepository
import com.diego.playlistmaker.domain.searchActv.use_case.ClearTrackHistoryUseCaseImpl
import com.diego.playlistmaker.domain.searchActv.use_case.GetTracksHistoryUseCaseImpl
import com.diego.playlistmaker.domain.searchActv.use_case.SaveTrackHistoryUseCaseImpl
import com.diego.playlistmaker.domain.searchActv.use_case.SearchTracksWebUseCaseImpl

object Creator {

    // Use Cases для истории треков
    fun provideGetTracksHistoryUseCase(context: Context): GetTracksHistoryUseCaseImpl {
        return GetTracksHistoryUseCaseImpl(provideTrackHistoryRepository(context))
    }

    fun provideSaveTrackHistoryUseCase(context: Context): SaveTrackHistoryUseCaseImpl {
        return SaveTrackHistoryUseCaseImpl(provideTrackHistoryRepository(context))
    }

    fun provideClearTrackHistoryUseCase(context: Context): ClearTrackHistoryUseCaseImpl {
        return ClearTrackHistoryUseCaseImpl(provideTrackHistoryRepository(context))
    }

    // Use Cases для веб-поиска (заглушка)
    fun provideSearchTracksUseCase(): SearchTracksWebUseCaseImpl {
        return SearchTracksWebUseCaseImpl(provideTrackWebRepository())
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