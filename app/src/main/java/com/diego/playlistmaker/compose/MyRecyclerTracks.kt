package com.diego.playlistmaker.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diego.playlistmaker.search.domain.models.Track

@Composable
fun MyRecyclerTracks(tracks: List<Track>, modifier: Modifier){
    LazyColumn(
        modifier = modifier
    ) {
        items(tracks) { track ->
            ComposeTrack(track)
        }
    }
}