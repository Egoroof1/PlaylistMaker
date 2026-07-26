package com.diego.playlistmaker.search.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.diego.playlistmaker.R
import com.diego.playlistmaker.compose.HistoryTracksInSearch
import com.diego.playlistmaker.compose.MyEditTextSearch
import com.diego.playlistmaker.compose.MyRecyclerTracks
import com.diego.playlistmaker.compose.MyToolbar
import com.diego.playlistmaker.compose.NoSignalView
import com.diego.playlistmaker.compose.ShowNotFound
import com.diego.playlistmaker.search.ui.view_model.SearchScreenState
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import com.diego.playlistmaker.search.ui.view_model.UserActions
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragmentWithComposeUI : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MyToolbar(stringResource(R.string.search))
                    MyEditTextSearch(viewModel)

                    HistoryOrSearchOrError(viewModel)
                }
            }
        }
    }

    @Composable
    fun HistoryOrSearchOrError(
        viewModel: SearchViewModel
    ) {
        val searchState by viewModel.searchState.observeAsState(SearchScreenState())

        Log.d("TAG", "HistoryOrSearchOrError: ${searchState.userActions}")
        when (searchState.userActions) {
            UserActions.SHOW_HISTORY -> {
                HistoryTracksInSearch(
                    tracks = searchState.historyTracks,
                    navController = navController,
                    viewModel = viewModel
                )
            }

            UserActions.SEARCH -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 140.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(44.dp),
                        color = Color(0xFF3772E7),
                        strokeWidth = 4.dp
                    )
                }
            }

            UserActions.SHOW_SEARCH_RESULT -> {
                MyRecyclerTracks(
                    tracks = searchState.searchTracks,
                    viewModel = viewModel,
                    onTrackClicked = { track ->
                        val action =
                            SearchFragmentWithComposeUIDirections.actionSearchFragmentToPlayerFragment(
                                track
                            )
                        navController.navigate(action)
                    }
                )
            }

            UserActions.SHOW_NOT_FOUND -> {

                ShowNotFound(
                    R.drawable.ic_not_found,
                    R.string.not_found
                )

            }

            UserActions.ERROR -> {
                NoSignalView(
                    onUpdateClick = {
                        viewModel.performSearch(viewModel.searchState.value?.lastSearchQuery ?: "")
                    }
                )
                Toast.makeText(
                    requireContext(),
                    viewModel.searchState.value?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}