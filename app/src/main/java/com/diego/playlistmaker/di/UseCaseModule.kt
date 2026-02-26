package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.domain.use_case.FavoriteRepositoryUseCase
import com.diego.playlistmaker.media.domain.use_case.FavoriteInteractor
import com.diego.playlistmaker.media.domain.use_case.HistoryRepositoryUseCase
import com.diego.playlistmaker.media.domain.use_case.HistoryInteractor
import com.diego.playlistmaker.search.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    single<SearchTracksWebUseCas> {
        SearchTracksWebUseCaseImpl(get())
    }

    single<FavoriteRepositoryUseCase> {
        FavoriteInteractor(get())
    }

    single<HistoryRepositoryUseCase> {
        HistoryInteractor(get())
    }
}