package com.diego.playlistmaker.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.diego.playlistmaker.R
import com.diego.playlistmaker.config.MyShared
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }


    private fun setupClickListeners() {
        val btnBack = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnShare = findViewById<TextView>(R.id.btn_search)
        val btnSupport = findViewById<TextView>(R.id.btn_support)
        val btnAgreement = findViewById<TextView>(R.id.btn_agreement)
        val swIsBlackTheme = findViewById<SwitchMaterial>(R.id.sw_isBlackTheme)

        // Устанавливаем состояние переключателя в соответствии с сохраненной темой
        swIsBlackTheme.isChecked = MyShared.getTheme()

        swIsBlackTheme.setOnCheckedChangeListener { _, isChecked ->
            MyShared.saveTheme(isChecked)
            MyShared.applyTheme()
        }

        btnBack.setNavigationOnClickListener { finish() }
        btnShare.setOnClickListener { shareApp() }
        btnSupport.setOnClickListener { contactSupport() }
        btnAgreement.setOnClickListener { openAgreement() }
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