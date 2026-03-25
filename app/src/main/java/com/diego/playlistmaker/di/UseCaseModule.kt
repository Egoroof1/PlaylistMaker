package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.domain.use_case.FavoriteInteractor
import com.diego.playlistmaker.media.domain.use_case.FavoriteInteractorImpl
import com.diego.playlistmaker.media.domain.use_case.HistoryInteractor
import com.diego.playlistmaker.media.domain.use_case.HistoryInteractorImpl
import com.diego.playlistmaker.media.domain.use_case.ImageStorageInteractor
import com.diego.playlistmaker.media.domain.use_case.ImageStorageInteractorImpl
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractorImpl
import com.diego.playlistmaker.media.domain.use_case.TrackInPlayListInteractor
import com.diego.playlistmaker.media.domain.use_case.TrackInPlayListInteractorImpl
import com.diego.playlistmaker.search.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    single<SearchTracksWebUseCas> {
        SearchTracksWebUseCaseImpl(get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    single<PlayListInteractor> {
        PlayListInteractorImpl(get())
    }

    single<TrackInPlayListInteractor> {
        TrackInPlayListInteractorImpl(get())
    }

    single<ImageStorageInteractor> {
        ImageStorageInteractorImpl(get())
    }
}