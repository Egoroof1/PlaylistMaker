package com.diego.playlistmaker.di

import com.diego.playlistmaker.media.ui.view_model.MediaViewModel
import com.diego.playlistmaker.media.ui.view_model.PlayListsFragmentViewModel
import com.diego.playlistmaker.media.ui.view_model.TracksFragmentViewModel
import com.diego.playlistmaker.player.ui.PlayerViewModel
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import com.diego.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Search ViewModel
    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            getTracksHistoryUseCase = get(),
            saveTrackHistoryUseCase = get(),
            clearTrackHistoryUseCase = get()
        )
    }

    // Settings ViewModel
    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }

    // Player ViewModel (не имеет зависимостей)
    viewModel {
        PlayerViewModel()
    }

    // Media Activity ViewModel
    viewModel {
        MediaViewModel()
    }

    // PlayLists Fragment ViewModel
    viewModel {
        PlayListsFragmentViewModel()
    }

    // Tracks Fragment ViewModel
    viewModel {
        TracksFragmentViewModel()
    }
}