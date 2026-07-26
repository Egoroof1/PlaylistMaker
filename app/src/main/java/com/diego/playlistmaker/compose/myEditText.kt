package com.diego.playlistmaker.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diego.playlistmaker.R
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MyEditTextSearch(
    viewModel: SearchViewModel
) {
    var text by remember { mutableStateOf(viewModel.searchState.value?.lastSearchQuery ?: "") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(text) {

        if (text.isNotEmpty()) {
            delay(2000.milliseconds)
        }
        Log.d("TAG", "Поиск по запросу: '$text' (debounce)")
        viewModel.performSearch(text)

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = colorResource(R.color.color_str_et_search)
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search // Кнопка "Поиск" на клавиатуре
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Обработка нажатия Enter/Search на клавиатуре
                    Log.d("TAG", "Нажата кнопка Search/Enter. Запрос: '$text'")

                    keyboardController?.hide() // Скрываем клавиатуру
                    focusManager.clearFocus() //Скрываем курсор

                    viewModel.performSearch(text)

                }
            ),

            cursorBrush = SolidColor(colorResource(R.color.color_bg_input_active)),

            decorationBox = { innerTextField: @Composable () -> Unit ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = colorResource(R.color.form),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_search_16),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search),
                                color = colorResource(R.color.color_edittext_hint),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                fontFamily = FontFamily(Font(R.font.ys_display_regular))
                            )
                        }
                        innerTextField()
                    }

                    if (text.isNotEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.ic_clear),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    text = ""
                                    keyboardController?.hide() // Скрываем клавиатуру
                                    focusManager.clearFocus() //Скрываем курсор
                                }
                        )
                    }
                }
            }
        )
    }
}