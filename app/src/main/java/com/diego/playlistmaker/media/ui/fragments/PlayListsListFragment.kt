package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.diego.playlistmaker.databinding.FragmentPlayListsListBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.presentation.PlayListAdapter
import com.diego.playlistmaker.media.ui.view_model.PlayListsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsListFragment : Fragment() {
    private var _binding: FragmentPlayListsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayListsFragmentViewModel by viewModel()
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
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.state.collect { state ->
                playListsAdapter.updateList(state.playLists)
                updateUI(state.playLists.isEmpty())
            }
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        binding.playlistsEmpty.isVisible = isEmpty
    }

    private fun onPlayListClicked(playList: PlayList) {
        if (!isClicked) {
            isClicked = true

            lifecycleScope.launch {
                isClicked = false
                delay(ANTY_DOUBLE_CLICK)
            }

            Log.d("TAG", "onPlayListClicked: CKLICK")

            // Переходим на PlayerFragment
//            val action = MediaFragmentDirections.actionMediaFragmentToPlayerFragment(playList.id)
//            findNavController().navigate(action)
        }
    }

    private fun setClickListeners() {
        binding.btnNewPlaylist.setOnClickListener {
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