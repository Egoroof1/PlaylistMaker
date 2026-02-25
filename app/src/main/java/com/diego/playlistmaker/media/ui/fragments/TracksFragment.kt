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

    private val tracks = mutableListOf<Track>()
    private var isClicked = false

    private val trackAdapter by lazy {
        TrackAdapter(tracks) {track -> onTrackClicked(track) }
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

        observeViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTracksFavorite.adapter = trackAdapter
    }

    private fun onTrackClicked(track: Track) {
        if (!isClicked) {
            isClicked = true

            viewLifecycleOwner.lifecycleScope.launch {
                isClicked = false
                delay(ANTY_DOUBLE_CLICK)
            }

            // Переходим на PlayerFragment
            val action = MediaFragmentDirections.actionMediaFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tracksState.collect { tracksState ->
                tracks.clear()
                tracks.addAll(tracksState.tracksList)
                binding.recyclerTracksFavorite.adapter?.notifyDataSetChanged()
                updateUI(tracksState)
            }
        }
    }

    private fun updateUI(tracksState: TracksState){
        if (tracksState.tracksList.isEmpty()) {
            binding.emptyFavorite.isVisible = true
        } else {
            binding.emptyFavorite.isVisible = false
        }
    }

    companion object {
        private const val ANTY_DOUBLE_CLICK = 500L

        @JvmStatic
        fun newInstance() =
            TracksFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}