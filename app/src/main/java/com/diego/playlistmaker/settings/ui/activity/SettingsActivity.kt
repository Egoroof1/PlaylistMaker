package com.diego.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.diego.playlistmaker.databinding.ActivitySettingsBinding
import com.diego.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel // Импорт Koin

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    // МЕНЯЕМ: получаем ViewModel через Koin (убираем фабрику)
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
        observeViewModel()

        Log.d("SettingsActivity", "Activity created")
    }

    private fun observeViewModel() {
        viewModel.themeSettings.observe(this, Observer { themeSettings ->
            Log.d("SettingsActivity", "Theme changed: ${themeSettings.isDarkTheme}")
            binding.swIsBlackTheme.isChecked = themeSettings.isDarkTheme
        })
    }

    private fun setupClickListeners() {
        // Устанавливаем начальное состояние переключателя из ViewModel
        viewModel.themeSettings.observe(this) { themeSettings ->
            binding.swIsBlackTheme.isChecked = themeSettings.isDarkTheme
        }

        binding.swIsBlackTheme.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsActivity", "Switch clicked: $isChecked")
            viewModel.updateTheme(isChecked)
        }

        binding.apply {
            toolbar.setNavigationOnClickListener {
                Log.d("SettingsActivity", "Back button clicked")
                finish()
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
}