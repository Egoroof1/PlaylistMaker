package com.diego.playlistmaker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R

@Composable
fun ShowNotFound(
    imageResources: Int,
    textResources: Int
){
    Column(
        modifier = Modifier
            .padding(top = 102.dp)
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageResources),
            contentDescription = null
        )

        Text(
            text = stringResource(textResources),
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 19.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.string),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
