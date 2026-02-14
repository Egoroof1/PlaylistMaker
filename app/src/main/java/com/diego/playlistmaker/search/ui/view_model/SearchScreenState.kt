package com.diego.playlistmaker.search.ui.view_model

import com.diego.playlistmaker.search.domain.models.Track

data class SearchScreenState(
    val historyTracks: List<Track> = emptyList(),
    val searchTracks: List<Track> = emptyList(),
    val userActions: UserActions = UserActions.SHOW_HISTORY,
    val message: String = ""
)

enum class UserActions {
    SEARCH,
    SHOW_SEARCH_RESULT,
    HIDE_SEARCH_RESULT,
    SHOW_NOT_FOUND,
    ERROR,
    SHOW_HISTORY
}