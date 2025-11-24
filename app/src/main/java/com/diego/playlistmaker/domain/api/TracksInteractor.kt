package com.diego.playlistmaker.domain.api

import com.diego.playlistmaker.domain.entities.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun onSuccess(tracks: List<Track>)
        fun onError(errorMessage: String)
    }
}