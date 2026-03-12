package com.diego.playlistmaker.media.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diego.playlistmaker.media.data.database.dao.TrackFavoriteDao
import com.diego.playlistmaker.media.data.database.dao.TrackHistoryDao
import com.diego.playlistmaker.media.data.database.entities.TrackFavoriteEntity
import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity

@Database(
    version = 1,
    entities = [
        TrackFavoriteEntity::class,
        TrackHistoryEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackFavoriteDao(): TrackFavoriteDao
    abstract fun trackHistoryDao(): TrackHistoryDao
}