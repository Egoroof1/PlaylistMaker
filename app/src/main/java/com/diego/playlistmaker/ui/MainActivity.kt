package com.diego.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.diego.playlistmaker.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners(){
        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMediaLibrary = findViewById<Button>(R.id.btn_media_library)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        btnSearch.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }

        btnMediaLibrary.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}