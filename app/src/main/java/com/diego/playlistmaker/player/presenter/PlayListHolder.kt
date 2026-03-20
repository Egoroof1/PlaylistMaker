package com.diego.playlistmaker.player.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ItemPlaylistHorizontalBinding
import com.diego.playlistmaker.media.domain.models.PlayList

class PlayListHolder(
    private val binding: ItemPlaylistHorizontalBinding,
    private val onPlayListClick: (PlayList) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var currentPlayList: PlayList? = null

    init {

        binding.root.setOnClickListener {
            currentPlayList?.let { playList ->
                onPlayListClick(playList)
            }
        }
    }

    fun bind(playList: PlayList) {
        currentPlayList = playList

        binding.etNamePlaylist.text = playList.name
        binding.tvItemPlaylistQuantityTracks.text = playList.quantityTracks.toString()

        Glide.with(binding.image)
            .load(playList.coverImagePath)
            .override(100, 100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.image)

    }

    companion object {
        fun from(parent: ViewGroup, onItemClick: (PlayList) -> Unit): PlayListHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistHorizontalBinding.inflate(inflater, parent, false)
            return PlayListHolder(binding, onItemClick)
        }
    }
}