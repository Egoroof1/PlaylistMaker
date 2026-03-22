package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.media.data.database.repository.FavoriteRepositoryImpl
import com.diego.playlistmaker.media.data.database.repository.FavoriteRepository
import com.diego.playlistmaker.media.data.database.repository.HistoryRepository
import com.diego.playlistmaker.media.data.database.repository.HistoryRepositoryImpl
import com.diego.playlistmaker.media.data.database.repository.PlayListRepository
import com.diego.playlistmaker.media.data.database.repository.PlayListRepositoryImpl
import com.diego.playlistmaker.media.data.database.repository.TrackInPlayListRepository
import com.diego.playlistmaker.media.data.database.repository.TrackInPlayListRepositoryImpl
import com.diego.playlistmaker.media.data.image_storage.ImageStorageImpl
import com.diego.playlistmaker.media.data.image_storage.ImageStorageRepository
import com.diego.playlistmaker.search.data.network.repository.TrackWebRepositoryImpl
import com.diego.playlistmaker.search.domain.repository.TrackWebRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackWebRepository> {
        TrackWebRepositoryImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get<AppDatabase>().trackFavoriteDao())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get<AppDatabase>().trackHistoryDao())
    }

    single<ImageStorageRepository> {
        ImageStorageImpl(get())
    }

    single<PlayListRepository> {
        PlayListRepositoryImpl(get<AppDatabase>().playListDao())
    }

    single<TrackInPlayListRepository> {
        TrackInPlayListRepositoryImpl(get<AppDatabase>().trackInPlayListDao())
    }
}