package com.diego.playlistmaker.media.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    suspend fun getPlayListById(playListId: Int): PlayListEntity

    @Query("SELECT * FROM play_list_table")
    fun getAllPlayList(): Flow<List<PlayListEntity>>

    @Query("SELECT * FROM track_in_play_list_table ORDER BY id DESC")
    fun getAllTracksForPlayList(): Flow<List<TrackInPlayListEntity>>
}