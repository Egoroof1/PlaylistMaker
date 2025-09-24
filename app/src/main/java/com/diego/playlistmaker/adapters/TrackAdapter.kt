package com.diego.playlistmaker.adapters

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
import com.diego.playlistmaker.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    class TrackHolder(item: View): RecyclerView.ViewHolder(item) {

        private val nameTrack: TextView = item.findViewById(R.id.name_track)
        private val artistName: TextView = item.findViewById(R.id.artist_name)
        private val timeTrack: TextView = item.findViewById(R.id.time_track)
        private val image: ImageView = item.findViewById(R.id.image)

        fun bind(track: Track){
            nameTrack.text = track.trackName
            artistName.text = track.artistName

//            val minutes = track.trackTimeMillis / 60000
//            val seconds = track.trackTimeMillis / 1000 % 60
//            timeTrack.text = String.format("%d:%02d", minutes, seconds)
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
                context.resources.displayMetrics).toInt()
        }
    }

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