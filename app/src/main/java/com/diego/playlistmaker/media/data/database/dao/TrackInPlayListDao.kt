package com.diego.playlistmaker.media.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diego.playlistmaker.media.data.database.entities.TrackInPlayListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackInPlayList(trackInPlayListEntity: TrackInPlayListEntity)

    @Query("DELETE FROM track_in_play_list_table WHERE trackId = :trackId AND playlistId = :playListId")
    suspend fun deleteTrackForPlayListById(trackId: Int, playListId: Int)

    @Query("SELECT * FROM track_in_play_list_table WHERE trackId = :trackId")
    suspend fun getTrackInPlayListByTrackId(trackId: Int): TrackInPlayListEntity?

    @Query("SELECT * FROM track_in_play_list_table WHERE playlistId = :playListId")
    fun getAllTracksInPlayListByIdPlaylist(playListId: Int): Flow<List<TrackInPlayListEntity>>

    @Query("DELETE FROM track_in_play_list_table WHERE playlistId = :playListId")
    suspend fun deleteAllTrackFromPlaylist(playListId: Int)
}