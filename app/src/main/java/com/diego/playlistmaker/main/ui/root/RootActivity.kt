package com.diego.playlistmaker.main.ui.root

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ActivityRootBinding
import com.diego.playlistmaker.media.ui.fragments.MediaFragment
import com.diego.playlistmaker.search.ui.fragment.SearchFragment
import com.diego.playlistmaker.settings.ui.fragment.SettingsFragment

class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootFragmentContainerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null){
            supportFragmentManager.commit {
//                this.add(R.id.rootFragmentContainerView, MediaFragment())
//                this.add(R.id.rootFragmentContainerView, SearchFragment())
                this.add(R.id.rootFragmentContainerView, MainFragment())
//                this.add(R.id.rootFragmentContainerView, SettingsFragment())
            }
        }

    }
}