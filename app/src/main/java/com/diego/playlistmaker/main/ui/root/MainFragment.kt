package com.diego.playlistmaker.main.ui.root

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentMainBinding
import com.diego.playlistmaker.media.ui.fragments.MediaFragment
import com.diego.playlistmaker.search.ui.fragment.SearchFragment
import com.diego.playlistmaker.settings.ui.fragment.SettingsFragment

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Кнопка поиска
        binding.btnSearch.setOnClickListener {
            openSearchFragment()
        }

        // Кнопка медиа
        binding.btnMedia.setOnClickListener {
            openMediaFragment()
        }

        // Кнопка настроек
        binding.btnSettings.setOnClickListener {
            openSettingsFragment()
        }
    }

    private fun openSearchFragment() {
        val searchFragment = SearchFragment.newInstance()

        requireActivity().supportFragmentManager.commit {
            replace(R.id.rootFragmentContainerView, searchFragment)
            addToBackStack("search")
            setReorderingAllowed(true)
        }
    }

    private fun openMediaFragment() {
        val mediaFragment = MediaFragment.newInstance()

        requireActivity().supportFragmentManager.commit {
            replace(R.id.rootFragmentContainerView, mediaFragment)
            addToBackStack("media")
            setReorderingAllowed(true)
        }
    }

    private fun openSettingsFragment() {
        val settingsFragment = SettingsFragment.newInstance()

        requireActivity().supportFragmentManager.commit {
            replace(R.id.rootFragmentContainerView, settingsFragment)
            addToBackStack("settings")
            setReorderingAllowed(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}