package com.diego.playlistmaker.media.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diego.playlistmaker.media.data.dao.TrackFavoriteDao
import com.diego.playlistmaker.media.data.dao.TrackHistoryDao
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

    abstract fun trackFavoriteDao(): TrackFavoriteDao
    abstract fun trackHistoryDao(): TrackHistoryDao
}