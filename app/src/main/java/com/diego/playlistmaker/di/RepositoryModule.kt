package com.diego.playlistmaker.di

import com.diego.playlistmaker.search.data.local_storage.repository.TrackHistoryRepositoryImpl
import com.diego.playlistmaker.search.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.diego.playlistmaker.search.domain.repository.TrackWebRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackWebRepository> {
        TrackWebRepositoryImpl(get())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get())
    }
}