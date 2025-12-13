package com.diego.playlistmaker.data.local_storage

import com.diego.playlistmaker.data.models.TrackDto

interface TrackDtoStorage {
    fun get(): List<TrackDto>
    fun save(trackDto: TrackDto): Boolean
    fun clear(): Boolean
}