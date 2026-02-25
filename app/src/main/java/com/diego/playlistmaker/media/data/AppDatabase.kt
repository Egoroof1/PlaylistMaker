package com.diego.playlistmaker.media.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diego.playlistmaker.media.data.dao.TrackDao
import com.diego.playlistmaker.media.data.entities.TrackFavoriteEntity
import com.diego.playlistmaker.media.data.entities.TrackHistoryEntity

@Database(
    version = 1,
    entities = [
        TrackFavoriteEntity::class,
        TrackHistoryEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}