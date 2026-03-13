package com.diego.playlistmaker.media.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.databinding.ItemPlaylistBinding
import com.diego.playlistmaker.media.domain.models.PlayList

class PlayListAdapter (
    var playLists: List<PlayList>,
    private val onItemClick: (PlayList) -> Unit //CallBack
) : RecyclerView.Adapter<PlayListAdapter.PlayListHolder>() {

    class PlayListHolder(
        private val binding: ItemPlaylistBinding,
        private val onPlayListClick: (PlayList) -> Unit // Передаём callback в Holder
    ): RecyclerView.ViewHolder(binding.root) {
        private var currentPlayList: PlayList? = null

        init {
            // Добавляем обработчик клика на весь элемент
            binding.root.setOnClickListener {
                currentPlayList?.let { playList ->
                    onPlayListClick(playList)
                }
            }
        }

        fun bind(playList: PlayList){
            currentPlayList = playList

            binding.tvPlaylistName.text = playList.name

            val quantityTracks = playList.quantityTracks
            binding.tvQuantityTracks.text = "$quantityTracks ${getPluralForm(quantityTracks)}"

            if (playList.coverImagePath.isNotEmpty()) {
                binding.ivCoverPlaylist.setImageURI(playList.coverImagePath.toUri())
            }
        }

        private fun getPluralForm(count: Int): String {
            return when {
                count % 10 == 1 && count % 100 != 11 -> "трек"
                count % 10 in 2..4 && (count % 100 !in 12..14) -> "трека"
                else -> "треков"
            }
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (PlayList) -> Unit): PlayListHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPlaylistBinding.inflate(inflater, parent, false)
                return PlayListHolder(binding, onItemClick)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListHolder {
        return PlayListHolder.from(parent, onItemClick)
    }

    override fun onBindViewHolder(
        holder: PlayListHolder,
        position: Int
    ) {
        holder.bind(playLists[position])
    }

    override fun getItemCount(): Int {
        return playLists.size
    }

}