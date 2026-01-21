package com.diego.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.diego.playlistmaker.databinding.FragmentSettingsBinding
import com.diego.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.themeSettings.observe(viewLifecycleOwner, Observer { themeSettings ->
            Log.d("SettingsActivity", "Theme changed: ${themeSettings.isDarkTheme}")
            binding.swIsBlackTheme.isChecked = themeSettings.isDarkTheme
        })
    }

    private fun setupClickListeners() {
        // Устанавливаем начальное состояние переключателя из ViewModel
        viewModel.themeSettings.observe(viewLifecycleOwner) { themeSettings ->
            binding.swIsBlackTheme.isChecked = themeSettings.isDarkTheme
        }

        binding.swIsBlackTheme.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsActivity", "Switch clicked: $isChecked")
            viewModel.updateTheme(isChecked)
        }

        binding.apply {
            toolbar.setNavigationOnClickListener {
                Log.d("SettingsActivity", "Back button clicked")
                parentFragmentManager.popBackStack()
            }
            btnSearch.setOnClickListener {
                Log.d("SettingsActivity", "Share button clicked")
                viewModel.shareApp()
            }
            btnSupport.setOnClickListener {
                Log.d("SettingsActivity", "Support button clicked")
                viewModel.contactSupport()
            }
            btnAgreement.setOnClickListener {
                Log.d("SettingsActivity", "Agreement button clicked")
                viewModel.openAgreement()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}