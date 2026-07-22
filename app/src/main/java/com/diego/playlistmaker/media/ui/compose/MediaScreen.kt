package com.diego.playlistmaker.media.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.diego.playlistmaker.R
import com.diego.playlistmaker.compose.MyRecyclerTracks
import com.diego.playlistmaker.compose.MyToolbar
import com.diego.playlistmaker.compose.ShowNotFound
import com.diego.playlistmaker.media.ui.view_model.PlayListsListViewModel
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel

@Composable
fun MediaScreen(
    tracks: List<Track>,
    navController: NavController,
    searchViewModel: SearchViewModel,
    playListViewModel: PlayListsListViewModel
) {
    val tabs = listOf(stringResource(R.string.tracks), stringResource(R.string.playlists))

    // Состояние для выбранной вкладки
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg_activity))
    ) {

        MyToolbar(textToolbar = stringResource(R.string.media))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {

                if (tracks.isNotEmpty()){
                    MyRecyclerTracks(
                        tracks = tracks,
                        viewModel = searchViewModel,
                        onTrackClicked = { track ->
                            val action =
                                MediaFragmentComposeDirections.actionMediaFragmentToPlayerFragment(track)
                            navController.navigate(action)
                        }
                    )
                } else {
                    ShowNotFound(
                        imageResources = R.drawable.ic_not_found,
                        textResources = R.string.your_library_is_empty
                    )
                }
            }

            1 -> PlayListsListCompose(
                viewModel = playListViewModel,
                onClickBtn = {
                    val action = MediaFragmentComposeDirections.actionMediaFragmentToAddMediaPlayerFragment()
                    navController.navigate(action)
                },
                navController
            )
        }
    }
}