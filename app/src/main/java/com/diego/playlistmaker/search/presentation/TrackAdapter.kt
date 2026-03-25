package com.diego.playlistmaker.search.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        return TrackHolder.from(parent, onTrackClick, onTrackLongClicked)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateList(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}