package com.diego.playlistmaker.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diego.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.media.setOnClickListener {
            finish()
        }
    }
}