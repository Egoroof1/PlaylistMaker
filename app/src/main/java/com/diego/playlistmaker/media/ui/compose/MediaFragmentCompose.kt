package com.diego.playlistmaker.media.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.diego.playlistmaker.R
import com.diego.playlistmaker.compose.MyRecyclerTracks
import com.diego.playlistmaker.compose.MyToolbar
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaFragmentCompose: Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen()
            }
        }
    }
}

@Composable
fun MediaScreen() {
//    val scope = rememberCoroutineScope()
//    val tabs = listOf("Плейлисты", "Избранное") // Замените на ваши заголовки
//
//    // Состояние для выбранной вкладки
//    var selectedTabIndex by remember { mutableIntStateOf(0) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(colorResource(R.color.bg_activity))
//    ) {
//        // Toolbar
//        MyToolbar(textToolbar = stringResource(R.string.media))
//
//        // TabLayout
//        TabRow(
//            selectedTabIndex = selectedTabIndex,
//            modifier = Modifier.fillMaxWidth(),
//            containerColor = Color.Transparent
//        ) {
//            tabs.forEachIndexed { index, title ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index },
//                    text = { Text(text = title) }
//                )
//            }
//        }
//
//        // Контент вкладок (ViewPager)
//        when (selectedTabIndex) {
//            0 -> MyRecyclerTracks(
//                tracks = listOf<Track>(),
//                navController = NavController(),
//                viewModel = ViewModel
//                )
//            1 -> FavoritesContent()
//        }
//    }
}

@Composable
fun FavoritesContent() {
    // Контент для избранного
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Избранное")
    }
}