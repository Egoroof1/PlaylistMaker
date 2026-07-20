package com.diego.playlistmaker.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R
import com.diego.playlistmaker.search.domain.models.Track

@Composable
fun HistoryTracksInSearch() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = stringResource(R.string.you_search),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 19.sp,
                fontWeight = FontWeight(500),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(R.color.string)
            )
        }

        val tracks = mutableListOf<Track>()

        for (i in 0..100) {
            tracks.add(
                Track(
                    i,
                    "Name",
                    "Artist",
                    "Albom",
                    "01.01.15",
                    "Rozk",
                    "USA",
                    195000L,
                    "https://img.goodfon.ru/wallpaper/nbig/c/c9/enot-vzgliad-voda-pogruzhenie-morda.webp",
                    ""
                )
            )
        }

        MyRecyclerTracks(
            tracks,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        MyButton(
            stringResource(R.string.clear_history)
        )
    }

}