package com.diego.playlistmaker.media.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity

@Dao
interface TrackInPlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity)

    @Query("DELETE FROM track_in_play_list_table WHERE trackId = :trackId")
    suspend fun deleteTrackForPlayListById(trackId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM track_in_play_list_table WHERE trackId = :trackId)")
    suspend fun isPlayList(trackId: Int): Boolean

    @Query("SELECT * FROM track_in_play_list_table WHERE trackId = :trackId")
    suspend fun getTrackInPlayListByIdTrack(trackId: Int): TrackInPlayListEntity?
}