package com.diego.playlistmaker.search.ui.view_model

import com.diego.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object ShowLoading : SearchState
    object HideSearchResults : SearchState
    object ShowSearchResults : SearchState
    object ShowNotFound : SearchState
    object HideHistory : SearchState
    object ClearSearchResults : SearchState
    object ClearHistory : SearchState
    data class ShowHistory(val tracks: List<Track>) : SearchState
    data class ShowSearchContent(val tracks: List<Track>) : SearchState
    data class ShowError(val message: String) : SearchState
}