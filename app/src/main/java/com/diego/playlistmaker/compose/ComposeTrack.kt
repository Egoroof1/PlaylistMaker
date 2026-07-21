package com.diego.playlistmaker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diego.playlistmaker.R
import com.diego.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ComposeTrack(
    track: Track,
    onTrackClick: (Track) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .padding(horizontal = 13.dp)
            .clickable { onTrackClick(track) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = colorResource(R.color.string),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.string),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Image(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null
                )
                Text(
                    text = SimpleDateFormat(
                        "m:ss",
                        Locale.getDefault()
                    ).format(track.trackTimeMillis),
                    fontSize = 11.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.string),
                    maxLines = 1
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = null
        )
    }
}