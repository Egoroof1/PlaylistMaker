package com.diego.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.diego.playlistmaker.search.data.local_storage.repository.TrackHistoryRepositoryImpl
import com.diego.playlistmaker.search.data.local_storage.shared_prefs.SharedPrefTrackStorage
import com.diego.playlistmaker.search.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.search.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.search.data.network.retrofit.RetrofitTrackWeb
import com.diego.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.diego.playlistmaker.search.domain.repository.TrackWebRepository
import com.diego.playlistmaker.search.domain.use_case.ClearTrackHistoryUseCaseImpl
import com.diego.playlistmaker.search.domain.use_case.GetTracksHistoryUseCaseImpl
import com.diego.playlistmaker.search.domain.use_case.SaveTrackHistoryUseCaseImpl
import com.diego.playlistmaker.search.domain.use_case.SearchTracksWebUseCaseImpl
import com.diego.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.diego.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.diego.playlistmaker.settings.domain.repository.SettingsInteractor
import com.diego.playlistmaker.settings.domain.repository.SettingsRepository
import com.diego.playlistmaker.sharing.data.impl.AndroidResourceProvider
import com.diego.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.diego.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.diego.playlistmaker.sharing.domain.repository.ResourceProvider
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    // Settings
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }

    // Sharing
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            provideExternalNavigator(),
            provideResourceProvider()
        )
    }

    private fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(provideContext())
    }

    private fun provideResourceProvider(): ResourceProvider {
        return AndroidResourceProvider(provideContext())
    }

    private fun provideContext(): Context = application.applicationContext

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

    // Use Cases для веб-поиска
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