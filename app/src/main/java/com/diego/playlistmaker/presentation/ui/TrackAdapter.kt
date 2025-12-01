package com.diego.playlistmaker.presentation.ui

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.domain.searchActv.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: List<Track>,
    private val onItemClick: (Track) -> Unit //CallBack
) : RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    class TrackHolder(
        item: View,
        private val onTrackClick: (Track) -> Unit // Передаём callback в Holder
    ): RecyclerView.ViewHolder(item) {

        private val nameTrack: TextView = item.findViewById(R.id.name_track)
        private val artistName: TextView = item.findViewById(R.id.artist_name)
        private val timeTrack: TextView = item.findViewById(R.id.time_track)
        private val image: ImageView = item.findViewById(R.id.image)
        private var currentTrack: Track? = null

        init {
            // Добавляем обработчик клика на весь элемент
            itemView.setOnClickListener {
                currentTrack?.let { track ->
                    onTrackClick(track)
                }
            }
        }

        fun bind(track: Track){
            currentTrack = track

            nameTrack.text = track.trackName
            artistName.text = track.artistName

            timeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(2f, itemView)))
                .into(image)
        }

        private fun dpToPx(dp: Float, context: View): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_track, parent, false)
        return TrackHolder(view, onItemClick)
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