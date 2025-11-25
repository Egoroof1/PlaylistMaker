package com.diego.playlistmaker.creator

import com.diego.playlistmaker.data.local.TrackLocalRepositoryImpl
import com.diego.playlistmaker.data.network.TrackWebRepositoryImpl
import com.diego.playlistmaker.domain.repository.TrackLocalRepository
import com.diego.playlistmaker.domain.repository.TrackWebRepository
import com.diego.playlistmaker.domain.use_case.ClearHistoryUseCase
import com.diego.playlistmaker.domain.use_case.GetTracksListLocalUseCase
import com.diego.playlistmaker.domain.use_case.GetTracksListWebUseCase
import com.diego.playlistmaker.domain.use_case.SaveTrackToHistoryUseCase

object Creator {

    // Use Cases для веб-поиска
    fun provideGetTracksListWebUseCase(): GetTracksListWebUseCase {
        return GetTracksListWebUseCase(provideTrackWebRepository())
    }

    // Use Cases для локальной истории
    fun provideGetTracksListLocalUseCase(): GetTracksListLocalUseCase {
        return GetTracksListLocalUseCase(provideTrackLocalRepository())
    }

    fun provideSaveTrackToHistoryUseCase(): SaveTrackToHistoryUseCase {
        return SaveTrackToHistoryUseCase(provideTrackLocalRepository())
    }

    fun provideClearHistoryUseCase(): ClearHistoryUseCase {
        return ClearHistoryUseCase(provideTrackLocalRepository())
    }

    // Репозитории
    private fun provideTrackWebRepository(): TrackWebRepository {
        return TrackWebRepositoryImpl()
    }

    private fun provideTrackLocalRepository(): TrackLocalRepository {
        return TrackLocalRepositoryImpl()
    }
}