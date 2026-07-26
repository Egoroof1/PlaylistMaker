package com.diego.playlistmaker.settings.ui.fragment

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.diego.playlistmaker.compose.MyToolbar
import com.diego.playlistmaker.settings.ui.view_model.SettingsViewModel

@Composable
fun SettingsScreen(
    context: Context,
    viewModel: SettingsViewModel
) {

    val shareText = context.getString(R.string.practicum_ru)


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MyToolbar(stringResource(R.string.settings))

        SettingsSwitchItem(
            text = stringResource(R.string.black_theme),
            isChecked = viewModel.themeSettings.value?.isDarkTheme ?: false,
            onCheckedChange = { isChecked ->
                viewModel.updateTheme(isChecked)
            }
        )

        SettingsItem(
            text = stringResource(R.string.share_app),
            iconRes = R.drawable.ic_share_24,
            onClick = {
                viewModel.shareApp(shareText)
            }
        )

        SettingsItem(
            text = stringResource(R.string.write_to_support),
            iconRes = R.drawable.ic_support_24,
            onClick = {
                viewModel.contactSupport()
            }
        )

        SettingsItem(
            text = stringResource(R.string.user_agreement),
            iconRes = R.drawable.ic_arrow_forward_24,
            onClick = {
                viewModel.openAgreement()
            }
        )
    }

}

@Composable
fun SettingsSwitchItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = colorResource(R.color.string)
        )
        androidx.compose.material.Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.switch_thumb),
                checkedTrackColor = colorResource(R.color.switch_track),
                uncheckedThumbColor = colorResource(R.color.switch_thumb),
                uncheckedTrackColor = colorResource(R.color.switch_track)
            )
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = colorResource(R.color.string)
        )
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = colorResource(R.color.color_icon_setting)
        )
    }
}