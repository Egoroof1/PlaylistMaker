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
import com.diego.playlistmaker.databinding.FragmentPlayListsBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.presentation.PlayListAdapter
import com.diego.playlistmaker.media.ui.view_model.PlayListsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment : Fragment() {
    private var _binding: FragmentPlayListsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayListsFragmentViewModel by viewModel()

    private val playListsAdapter by lazy {
        PlayListAdapter(playLists) { playList -> onPlayListClicked(playList)}
    }
    private var playLists = emptyList<PlayList>()
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
        _binding = FragmentPlayListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setClickListeners()
        recycler()
        updateUI()
    }

    private fun recycler(){
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
                playLists = state.playLists
                playListsAdapter.updateList(playLists)
                updateUI()
                Log.d("TAG", "observeViewModel: ${playLists.size}\n${playLists}")
            }
        }

    }

    private fun updateUI(){
        binding.playlistsEmpty.isVisible = playLists.isEmpty()
    }

    private fun onPlayListClicked(playList: PlayList) {
        if (!isClicked) {
            isClicked = true

            lifecycleScope.launch(Dispatchers.Main) {
                isClicked = false
                delay(ANTY_DOUBLE_CLICK)
            }

            Log.d("TAG", "onPlayListClicked: CLICK")

//            viewModel.saveTrackToHistory(track)

            // Переходим на PlayerFragment
//            val action = MediaFragmentDirections.actionMediaFragmentToPlayerFragment(playLists)
//            findNavController().navigate(action)
        }
    }

    private fun setClickListeners(){
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