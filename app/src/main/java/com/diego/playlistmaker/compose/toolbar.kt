package com.diego.playlistmaker.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R

@Composable
fun MyToolbar(
    textToolbar: String
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = textToolbar,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            fontWeight = FontWeight(500),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = colorResource(R.color.string)
        )
    }
}
