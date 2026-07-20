package com.diego.playlistmaker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.diego.playlistmaker.R
import com.diego.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ComposeTrack(track: Track){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(text = track.trackName)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName
                )
                Image(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null
                )
                Text(
                    text = SimpleDateFormat("m:ss", Locale.getDefault()).format(track.trackTimeMillis)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = null
        )
    }
}