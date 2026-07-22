package com.diego.playlistmaker.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R

@Composable
fun NoSignalView(
    onUpdateClick: () -> Unit = {}
) {
    ShowNotFound(
        R.drawable.ic_not_signal,
        R.string.problems_signal
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = onUpdateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.string),
                contentColor = colorResource(R.color.btn_update_text)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
        ) {
            Text(
                text = stringResource(R.string.update),
                fontSize = 14.sp,
                color = colorResource(R.color.btn_update_text)
            )
        }
    }
}