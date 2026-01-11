package com.diego.playlistmaker.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diego.playlistmaker.databinding.FragmentPlayListsBinding

class PlayListsFragment : Fragment() {

    val fragmentPlayList = "FragmentPlayList"
    private var _binding: FragmentPlayListsBinding? = null
    private val binding get() = _binding!!

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

        // Исправляем возвращаемое значение
        binding.textView.text = fragmentPlayList

        return binding.root
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