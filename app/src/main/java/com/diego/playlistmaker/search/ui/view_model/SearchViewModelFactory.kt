package com.diego.playlistmaker.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diego.playlistmaker.creator.Creator

class SearchViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchTracksUseCase = Creator.provideSearchTracksUseCase(),
                getTracksHistoryUseCase = Creator.provideGetTracksHistoryUseCase(),
                saveTrackHistoryUseCase = Creator.provideSaveTrackHistoryUseCase(),
                clearTrackHistoryUseCase = Creator.provideClearTrackHistoryUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}