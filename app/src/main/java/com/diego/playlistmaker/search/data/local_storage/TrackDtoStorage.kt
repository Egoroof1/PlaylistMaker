package com.diego.playlistmaker.search.data.local_storage

import com.diego.playlistmaker.search.data.models.TrackDto

interface TrackDtoStorage {
    fun get(): List<TrackDto>
    fun save(trackDto: TrackDto): Boolean
    fun clear(): Boolean
}