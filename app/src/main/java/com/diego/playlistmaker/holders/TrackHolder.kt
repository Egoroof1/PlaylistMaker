package com.diego.playlistmaker.holders

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.models.Track

class TrackHolder(item: View): RecyclerView.ViewHolder(item) {

    private val nameTrack: TextView = item.findViewById(R.id.name_track)
    private val artistName: TextView = item.findViewById(R.id.artist_name)
    private val timeTrack: TextView = item.findViewById(R.id.time_track)
    private val image: ImageView = item.findViewById(R.id.image)

    fun bind(track: Track){
        nameTrack.text = track.trackName
        artistName.text = track.artistName
        timeTrack.text = track.trackTime

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