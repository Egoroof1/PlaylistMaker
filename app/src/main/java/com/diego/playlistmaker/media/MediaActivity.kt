package com.diego.playlistmaker.media

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ActivityMediaBinding
import com.diego.playlistmaker.media.presentation.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.media) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setOnClickListener {
            finish()
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = PagerAdapter(hostFragment = this)
        binding.pager.adapter = adapter

        // Связываем TabLayout с ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }.attach()
    }

}