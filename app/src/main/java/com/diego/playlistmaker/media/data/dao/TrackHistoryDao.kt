package com.diego.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diego.playlistmaker.media.data.entities.TrackHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackHistoryEntity)

    @Query("SELECT * FROM track_history ORDER BY id DESC")
    fun getTracks(): Flow<List<TrackHistoryEntity>>

    @Query("SELECT * FROM track_history")
    suspend fun getHistoryTracks(): List<TrackHistoryEntity>

    @Query("DELETE FROM track_history")
    suspend fun deleteAll()

    @Query("DELETE FROM track_history WHERE id = (SELECT MIN(id) FROM track_history)")
    suspend fun deleteFirstTrack()

    @Query("DELETE FROM track_history WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Int)
}