package com.diego.playlistmaker.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R

@Composable
fun MyButton(
    text: String
){
    Button(
        onClick = {

        },
        modifier = Modifier
            .padding(vertical = 24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.string)
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.btn_update_text)
        )
    }
}