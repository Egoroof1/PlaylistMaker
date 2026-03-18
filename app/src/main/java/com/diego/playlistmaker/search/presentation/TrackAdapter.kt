package com.diego.playlistmaker.search.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.search.domain.models.Track

class TrackAdapter(
    var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackHolder {
        return TrackHolder.from(parent, onTrackClick)
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

    fun updateList(newTracks: List<Track>) {
        tracks = newTracks
        notifyItemChanged(0)
    }

}