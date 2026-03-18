package com.diego.playlistmaker.media.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.diego.playlistmaker.databinding.FragmentTracksBinding
import com.diego.playlistmaker.media.ui.state.TracksState
import com.diego.playlistmaker.media.ui.view_model.TracksFragmentViewModel
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.presentation.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracksFragment : Fragment() {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TracksFragmentViewModel by viewModel()
    private var isClicked = false
    private val trackAdapter by lazy {
        TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        binding.recyclerTracksFavorite.adapter = trackAdapter
    }

    private fun onTrackClicked(track: Track) {
        if (!isClicked) {
            isClicked = true

            lifecycleScope.launch {
                isClicked = false
                delay(ANTY_DOUBLE_CLICK)
            }

            viewModel.saveTrackToHistory(track)

            // Переходим на PlayerFragment
            val action = MediaFragmentDirections.actionMediaFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tracksState.collect { tracksState ->
                binding.progressBar.isVisible = true
                trackAdapter.updateList(tracksState.tracksList)
                binding.progressBar.isVisible = false

                updateUI(tracksState)
            }
        }
    }

    private fun updateUI(tracksState: TracksState){
            binding.emptyFavorite.isVisible = tracksState.tracksList.isEmpty()
    }

    companion object {
        private const val ANTY_DOUBLE_CLICK = 500L
    }
}