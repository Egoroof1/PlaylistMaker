package com.diego.playlistmaker.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diego.playlistmaker.databinding.FragmentTracksBinding

class TracksFragment : Fragment() {

    private val fragmentTracks = "Fragment Tracks"

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

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

        binding.textView.text = fragmentTracks

        return binding.root
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