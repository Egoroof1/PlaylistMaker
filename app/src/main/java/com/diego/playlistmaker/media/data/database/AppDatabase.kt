package com.diego.playlistmaker.media.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diego.playlistmaker.media.data.database.dao.PlayListDao
import com.diego.playlistmaker.media.data.database.dao.TrackFavoriteDao
import com.diego.playlistmaker.media.data.database.dao.TrackHistoryDao
import com.diego.playlistmaker.media.data.database.dao.TrackInPlayListDao
import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackFavoriteEntity
import com.diego.playlistmaker.media.data.database.entities.TrackHistoryEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity

@Database(
    version = 1,
    entities = [
        TrackFavoriteEntity::class,
        TrackHistoryEntity::class,
        PlayListEntity::class,
        TrackInPlayListEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackFavoriteDao(): TrackFavoriteDao
    abstract fun trackHistoryDao(): TrackHistoryDao
    abstract fun playListDao(): PlayListDao
    abstract fun trackInPlayListDao(): TrackInPlayListDao
}