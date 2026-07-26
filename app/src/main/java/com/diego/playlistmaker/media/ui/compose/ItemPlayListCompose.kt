package com.diego.playlistmaker.media.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diego.playlistmaker.R
import com.diego.playlistmaker.media.domain.models.PlayList

@Composable
fun PlaylistItem(
    playlist: PlayList,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .padding(bottom = 16.dp)
            .clickable(
                onClick = {
                    onClick()
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Обложка (квадратная)
        AsyncImage(
            model = playlist.coverImagePath,
            contentDescription = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 1:1 соотношение
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop
        )

        Text(
            text = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            fontSize = 12.sp,
            color = colorResource(R.color.string),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = pluralStringResource(
                id = R.plurals.tracks_count,
                count = playlist.quantityTracks,
                playlist.quantityTracks // Форматирование с числом
            ),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = colorResource(R.color.string),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}