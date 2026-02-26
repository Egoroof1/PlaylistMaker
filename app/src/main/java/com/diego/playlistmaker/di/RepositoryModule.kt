package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.data.repository.FavoriteRepositoryImpl
import com.diego.playlistmaker.media.data.repository.FavoriteRepository
import com.diego.playlistmaker.media.data.repository.HistoryRepository
import com.diego.playlistmaker.media.data.repository.HistoryRepositoryImpl
import com.diego.playlistmaker.search.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.search.domain.repository.TrackWebRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackWebRepository> {
        TrackWebRepositoryImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
}