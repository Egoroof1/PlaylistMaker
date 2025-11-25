package com.diego.playlistmaker.domain.entity

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object NotFound : SearchResult()
    object NetworkError : SearchResult()
}