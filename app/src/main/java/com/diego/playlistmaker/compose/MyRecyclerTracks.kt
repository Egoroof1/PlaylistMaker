package com.diego.playlistmaker.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel

@Composable
fun MyRecyclerTracks(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onTrackClicked: (Track) -> Unit
){
    LazyColumn(
        modifier = modifier
    ) {
        items(tracks) { track ->
            ComposeTrack(
                track,
                onTrackClick = {
                    viewModel.saveTrackToHistory(track)
                    onTrackClicked(track)
                }
            )
        }
    }
}