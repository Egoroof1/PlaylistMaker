package com.diego.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.diego.playlistmaker.R
import com.diego.playlistmaker.config.MyShared
import com.diego.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

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
    }


    private fun setupClickListeners() {

        // Устанавливаем состояние переключателя в соответствии с сохраненной темой
        binding.swIsBlackTheme.isChecked = MyShared.getTheme()

        binding.swIsBlackTheme.setOnCheckedChangeListener { _, isChecked ->
            MyShared.saveTheme(isChecked)
            MyShared.applyTheme()
        }

        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            btnSearch.setOnClickListener { shareApp() }
            btnSupport.setOnClickListener { contactSupport() }
            btnAgreement.setOnClickListener { openAgreement() }
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_ru))
        }

        startActivity(shareIntent)
    }

    private fun contactSupport() {
        val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_developers)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_to_email))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.message_to_email))
        }

        startActivity(shareIntent)
    }

    private fun openAgreement() {
        val url = getString(R.string.practicum_offer_ru)
        val agreementIntent = Intent(Intent.ACTION_VIEW, url.toUri())

        startActivity(agreementIntent)

    }
}