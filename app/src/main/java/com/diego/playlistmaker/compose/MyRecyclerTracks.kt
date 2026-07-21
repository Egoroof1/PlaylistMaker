package com.diego.playlistmaker.compose

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.ui.fragment.SearchFragmentWithComposeUIDirections
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel

@Composable
fun MyRecyclerTracks(
    tracks: List<Track>,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SearchViewModel
){
    LazyColumn(
        modifier = modifier
    ) {
        items(tracks) { track ->
            ComposeTrack(
                track,
                onTrackClick = {
                    Log.d("TAG", "MyRecyclerTracks: track ${track.trackId}")

                    viewModel.saveTrackToHistory(track)

                    val action = SearchFragmentWithComposeUIDirections.actionSearchFragmentToPlayerFragment(track)
                    navController.navigate(action)
                }
            )
        }
    }
}