package com.diego.playlistmaker.media.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.ui.view_model.PlayListsListViewModel

@Composable
fun PlayListsListCompose(
    viewModel: PlayListsListViewModel,
    onClickBtn: () -> Unit,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClickBtn,
            modifier = Modifier
                .padding(vertical = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.string)
            )
        ) {
            Text(
                text = stringResource(R.string.mew_playlist),
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(R.color.btn_update_text)
            )
        }

        val list = viewModel.state.collectAsState().value.playLists

        //MyRecyclerPlayList
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.size) { index ->

                val playList = list[index]

                PlaylistItem(
                    playlist = playList,
                    onClick = {
                        val action = MediaFragmentComposeDirections.actionMediaFragmentToPlayListFragment(playList.id)
                        navController.navigate(action)
                    }
                )
            }
        }
    }
}