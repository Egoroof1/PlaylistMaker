package com.diego.playlistmaker.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.diego.playlistmaker.R
import com.diego.playlistmaker.compose.HistoryTracksInSearch
import com.diego.playlistmaker.compose.MyEditTextSearch
import com.diego.playlistmaker.compose.MyToolbar

class SearchFragmentWithComposeUI: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MyToolbar(stringResource(R.string.search))
                    MyEditTextSearch()
                    HistoryTracksInSearch()
                }
            }
        }
    }
}