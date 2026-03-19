package com.diego.playlistmaker.media.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.diego.playlistmaker.R
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

            binding.tvItemPlaylistName.text = playList.name

            val quantityTracks = playList.quantityTracks
            binding.tvItemPlaylistQuantityTracks.text = getTracksCountText(quantityTracks)

            if (playList.coverImagePath.isNotEmpty()) {
                Glide.with(binding.ivCoverItemPlaylist.context)
                    .load(playList.coverImagePath) // может быть File, String, Uri
                    .placeholder(R.drawable.placeholder) // показываем, пока грузится
                    .error(R.drawable.placeholder) // если ошибка загрузки
                    .centerCrop()
                    .override(160, 160) // РЕСАЙЗИМ под размер ImageView
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // КЭШИРУЕМ на диск
                    .skipMemoryCache(false) // используем memory cache
                    .into(binding.ivCoverItemPlaylist)
            } else {
                // Если путь пустой - ставим плейсхолдер
                binding.ivCoverItemPlaylist.setImageResource(R.drawable.placeholder)
            }
        }

        private fun getTracksCountText(count: Int): String {
            return when {
                count % 10 == 1 && count % 100 != 11 -> "$count трек"
                count % 10 in 2..4 && (count % 100 !in 12..14) -> "$count трека"
                else -> "$count треков"
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

    fun updateList(newList: List<PlayList>) {
        playLists = newList
        notifyItemRangeChanged(0, playLists.size)
    }

}