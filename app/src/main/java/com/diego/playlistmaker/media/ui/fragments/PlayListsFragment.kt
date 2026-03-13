package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.diego.playlistmaker.databinding.FragmentPlayListsBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.presentation.PlayListAdapter
import com.diego.playlistmaker.media.ui.view_model.PlayListsFragmentViewModel
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
    private val playLists = listOf(
        PlayList(name = "1111", description = "descr", quantityTracks = 15),
        PlayList(name = "222", description = "descr", quantityTracks = 1),
        PlayList(name = "333", description = "descr", quantityTracks = 4),
    )
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

        observeViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        recycler()
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
        // Здесь можно добавить observer'ы для LiveData из ViewModel
        // Например:
        // viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
        //     // Обновить RecyclerView с плейлистами
        // }
    }

    private fun onPlayListClicked(playList: PlayList) {
        if (!isClicked) {
            isClicked = true

            viewLifecycleOwner.lifecycleScope.launch {
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