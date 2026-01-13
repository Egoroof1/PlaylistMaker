package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diego.playlistmaker.databinding.FragmentPlayListsBinding
import com.diego.playlistmaker.media.ui.view_model.PlayListsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment : Fragment() {
    private var _binding: FragmentPlayListsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayListsFragmentViewModel by viewModel()

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

        // Наблюдаем за LiveData из ViewModel
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        // Здесь можно добавить observer'ы для LiveData из ViewModel
        // Например:
        // viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
        //     // Обновить RecyclerView с плейлистами
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayListsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}