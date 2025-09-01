package com.diego.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.R
import com.diego.playlistmaker.holders.TrackHolder
import com.diego.playlistmaker.models.Track

class TrackAdapter(
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_track, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}