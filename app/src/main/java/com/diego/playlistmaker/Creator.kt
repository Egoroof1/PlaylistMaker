package com.diego.playlistmaker

import android.app.Application
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

    private lateinit var application: Application

    fun initApplication(application: Application){
        this.application = application
    }

    // Use Cases для истории треков
    fun provideGetTracksHistoryUseCase(): GetTracksHistoryUseCaseImpl {
        return GetTracksHistoryUseCaseImpl(provideTrackHistoryRepository())
    }

    fun provideSaveTrackHistoryUseCase(): SaveTrackHistoryUseCaseImpl {
        return SaveTrackHistoryUseCaseImpl(provideTrackHistoryRepository())
    }

    fun provideClearTrackHistoryUseCase(): ClearTrackHistoryUseCaseImpl {
        return ClearTrackHistoryUseCaseImpl(provideTrackHistoryRepository())
    }

    // Use Cases для веб-поиска (заглушка)
    fun provideSearchTracksUseCase(): SearchTracksWebUseCaseImpl {
        return SearchTracksWebUseCaseImpl(provideTrackWebRepository())
    }

    // Репозитории
    private fun provideTrackHistoryRepository(): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(provideTrackDtoStorage())
    }

    private fun provideTrackWebRepository(): TrackWebRepository {
        return TrackWebRepositoryImpl(provideTrackDtoRetrofit())
    }

    // Network
    private fun provideTrackDtoRetrofit(): TrackDtoRetrofit {
        return RetrofitTrackWeb()
    }

    // Storage
    private fun provideTrackDtoStorage(): SharedPrefTrackStorage {
        return SharedPrefTrackStorage(application)
    }
}