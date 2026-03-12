package com.diego.playlistmaker.media.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diego.playlistmaker.media.data.database.entities.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackFavoriteEntity)

    @Query("SELECT * FROM tracks_favorite_table ORDER BY id DESC")
    fun getTracks(): Flow<List<TrackFavoriteEntity>>

    @Query("SELECT trackId FROM tracks_favorite_table WHERE trackId = :trackId")
    suspend fun isFavorite(trackId: Int): Boolean

    @Query("DELETE FROM tracks_favorite_table WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Int)
}