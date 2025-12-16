package com.diego.playlistmaker.presentation

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentTrackBinding
import com.diego.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: List<Track>,
    private val onItemClick: (Track) -> Unit //CallBack
) : RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    class TrackHolder(
        private val binding: FragmentTrackBinding,
        private val onTrackClick: (Track) -> Unit // Передаём callback в Holder
    ): RecyclerView.ViewHolder(binding.root) {
        private var currentTrack: Track? = null

        init {
            // Добавляем обработчик клика на весь элемент
            binding.root.setOnClickListener {
                currentTrack?.let { track ->
                    onTrackClick(track)
                }
            }
        }

        fun bind(track: Track){
            currentTrack = track

            binding.nameTrack.text = track.trackName
            binding.artistName.text = track.artistName

            binding.timeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(2f, itemView)))
                .into(binding.image)
        }

        private fun dpToPx(dp: Float, context: View): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (Track) -> Unit): TrackHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentTrackBinding.inflate(inflater, parent, false)
                return TrackHolder(binding, onItemClick)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackHolder {
        return TrackHolder.from(parent, onItemClick)
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