package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diego.playlistmaker.databinding.FragmentTracksBinding
import com.diego.playlistmaker.media.ui.view_model.TracksFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracksFragment : Fragment() {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TracksFragmentViewModel by viewModel()

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

    private fun observeViewModel() {
        // Здесь можно добавить observer'ы для LiveData из ViewModel
        // Например:
        // viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
        //     // Обновить RecyclerView с треками
        // }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TracksFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}