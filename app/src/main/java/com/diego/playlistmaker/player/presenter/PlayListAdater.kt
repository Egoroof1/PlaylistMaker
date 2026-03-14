package com.diego.playlistmaker.player.presenter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.media.domain.models.PlayList

class PlayListAdapter(
    var playLists: List<PlayList>,
    private val onPlayListClick: (PlayList) -> Unit
) : RecyclerView.Adapter<PlayListHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListHolder {
        return PlayListHolder.from(parent, onPlayListClick)
    }

    override fun onBindViewHolder(
        holder: PlayListHolder,
        position: Int
    ) {
        holder.bind(playLists[position])
    }

    override fun getItemCount(): Int = playLists.size

}