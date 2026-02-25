package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.diego.playlistmaker.databinding.FragmentTracksBinding
import com.diego.playlistmaker.media.ui.view_model.TracksFragmentViewModel
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.presentation.TrackAdapter
import com.diego.playlistmaker.search.ui.fragment.SearchFragmentDirections
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

    // в момент вызова onCreateView создаётся View для Fragment, поэтому именно в этот момент мы инициализируем binding и настраиваем View-элементы
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
            val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        // Здесь можно добавить observer'ы для LiveData из ViewModel
        // Например:
        // viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
        //     // Обновить RecyclerView с треками
        // }

        // Подписка на StateFlow (нужен lifecycleScope)
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle автоматически отписывается при остановке
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracksState.collect { state ->
                    tracks.clear()
                    tracks.addAll(state.tracksList)
                    trackAdapter.notifyDataSetChanged()
                }
            }
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