package com.diego.playlistmaker.media.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_list_table")
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverImagePath: String = "",
    val quantityTracks: Int = 0,
    val totalTimeMillis: Long = 0
)
