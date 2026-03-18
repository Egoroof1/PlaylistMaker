package com.diego.playlistmaker.media.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diego.playlistmaker.media.data.database.entities.PlayListEntity
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playListEntity: PlayListEntity)

    @Query("DELETE FROM play_list_table WHERE id = :playListId")
    suspend fun deletePlayListById(playListId: Int)

    @Query("SELECT * FROM play_list_table WHERE id = :playListId")
    suspend fun getPlayListById(playListId: Int): PlayListEntity?

    @Query("SELECT * FROM play_list_table ORDER BY id DESC")
    fun getAllPlayList(): Flow<List<PlayListEntity>>

    @Query("SELECT * FROM track_in_play_list_table ORDER BY id DESC")
    fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>>

    @Update
    suspend fun updatePlayList(playListEntity: PlayListEntity)

    @Query("UPDATE play_list_table SET quantityTracks = quantityTracks + 1 WHERE id = :playListId")
    suspend fun incrementTracksCount(playListId: Int)

    @Query("UPDATE play_list_table SET totalTimeMillis = totalTimeMillis + :timeMillis WHERE id = :playListId")
    suspend fun updateTotalTimeMillis(playListId: Int, timeMillis: Long)

    @Query("UPDATE play_list_table SET quantityTracks = quantityTracks - 1 WHERE id = :playListId")
    suspend fun decrementTracksCount(playListId: Int)
}