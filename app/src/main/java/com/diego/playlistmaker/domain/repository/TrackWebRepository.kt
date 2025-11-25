package com.diego.playlistmaker.domain.repository

import com.diego.playlistmaker.domain.entity.SearchResult

interface TrackWebRepository {
    fun getTracksForWeb(query: String, callback: (SearchResult) -> Unit)
}