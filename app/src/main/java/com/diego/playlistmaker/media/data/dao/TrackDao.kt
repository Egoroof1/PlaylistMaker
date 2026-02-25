package com.diego.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diego.playlistmaker.media.data.entities.TrackFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackFavoriteEntity)

    @Query("SELECT * FROM tracks_favorite_table ORDER BY id DESC")
    fun getTracks(): Flow<List<TrackFavoriteEntity>>

    @Query("SELECT trackId FROM tracks_favorite_table WHERE trackId = :trackId")
    suspend fun isFavorite(trackId: Int): Boolean

    @Query("DELETE FROM tracks_favorite_table WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Int)
}