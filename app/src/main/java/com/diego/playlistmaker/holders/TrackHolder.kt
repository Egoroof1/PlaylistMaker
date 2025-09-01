package com.diego.playlistmaker.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diego.playlistmaker.R
import com.diego.playlistmaker.models.Track

class TrackHolder(item: View): RecyclerView.ViewHolder(item) {

    fun bind(track: Track){
        itemView.findViewById<TextView>(R.id.name_track).text = track.trackName
        itemView.findViewById<TextView>(R.id.artist_name).text = track.artistName
        itemView.findViewById<TextView>(R.id.time_track).text = track.trackTime

        val image = itemView.findViewById<ImageView>(R.id.image)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(image)
    }
}