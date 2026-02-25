package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.domain.use_case.FavoriteRepositoryUseCase
import com.diego.playlistmaker.media.domain.use_case.FavoriteRepositoryUseCaseImpl
import com.diego.playlistmaker.search.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    single<SearchTracksWebUseCas> {
        SearchTracksWebUseCaseImpl(get())
    }

    single<GetTracksHistoryUseCase> {
        GetTracksHistoryUseCaseImpl(get())
    }

    single<SaveTrackHistoryUseCase> {
        SaveTrackHistoryUseCaseImpl(get())
    }

    single<ClearTrackHistoryUseCase> {
        ClearTrackHistoryUseCaseImpl(get())
    }

    single<FavoriteRepositoryUseCase> {
        FavoriteRepositoryUseCaseImpl(get())
    }
}