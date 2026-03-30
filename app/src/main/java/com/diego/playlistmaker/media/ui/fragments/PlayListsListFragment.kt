package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayListsListBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.presentation.PlayListAdapter
import com.diego.playlistmaker.media.ui.view_model.PlayListsListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsListFragment : Fragment() {
    private var _binding: FragmentPlayListsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayListsListViewModel by viewModel()
    private var newNamePlayList: String = ""
    private val playListsAdapter by lazy {
        PlayListAdapter(emptyList()) { playList -> onPlayListClicked(playList) }
    }
    private var isClicked = false

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
        _binding = FragmentPlayListsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setClickListeners()
        setupRecycler()
    }

    private fun setupRecycler() {
        val layoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false
        )

        binding.playListsRecycler.layoutManager = layoutManager
        binding.playListsRecycler.adapter = playListsAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            newNamePlayList = viewModel.state.value.nameNewPlayList

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (_binding != null) {
                        playListsAdapter.updateList(state.playLists)

                        updateUI(state.playLists.isEmpty(), newNamePlayList)
                    }
                }
            }
        }
    }

    private fun updateUI(isEmpty: Boolean, newNamePlayList: String) {
        binding.playlistsEmpty.isVisible = isEmpty

        if (newNamePlayList.isNotEmpty()){
            viewLifecycleOwner.lifecycleScope.launch {
                if (_binding != null) {
                    binding.tvNamePlaylist.text =
                        getString(R.string.playlist_is_create, newNamePlayList)
                    binding.viewNamePlaylist.isVisible = true
                    delay(2000)
                    binding.viewNamePlaylist.isVisible = false

                    viewModel.resetNewPlayListNameManually()
                }
            }
        }

    }

    private fun onPlayListClicked(playList: PlayList) {
        if (!isClicked) {
            isClicked = true

            lifecycleScope.launch {
                isClicked = false
                delay(ANTY_DOUBLE_CLICK)
            }

            val action = MediaFragmentDirections.actionMediaFragmentToPlayListFragment(playList.id)
            findNavController().navigate(action)
        }
    }

    private fun setClickListeners() {
        binding.btnCreateNewPlaylist.setOnClickListener {
            val action = MediaFragmentDirections.actionMediaFragmentToAddMediaPlayerFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ANTY_DOUBLE_CLICK = 500L
    }
}