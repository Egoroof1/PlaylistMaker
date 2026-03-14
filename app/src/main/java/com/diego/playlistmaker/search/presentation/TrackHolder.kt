package com.diego.playlistmaker.search.presentation

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ItemTrackBinding
import com.diego.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackHolder(
    private val binding: ItemTrackBinding,
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
            val binding = ItemTrackBinding.inflate(inflater, parent, false)
            return TrackHolder(binding, onItemClick)
        }
    }
}