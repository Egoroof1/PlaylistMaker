package com.diego.playlistmaker.media.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.diego.playlistmaker.media.ui.view_model.TracksFragmentViewModel
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import androidx.compose.runtime.collectAsState
import com.diego.playlistmaker.media.ui.view_model.PlayListsListViewModel

class MediaFragmentCompose : Fragment() {

    private val tracksViewModel: TracksFragmentViewModel by viewModel()
    private val searchViewModel: SearchViewModel by viewModel()
    private val playListsListViewModel: PlayListsListViewModel by viewModel()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen(
                    tracks = tracksViewModel.tracksState.collectAsState().value.tracksList,
                    navController = navController,
                    searchViewModel = searchViewModel,
                    playListViewModel = playListsListViewModel
                )
            }
        }
    }
}