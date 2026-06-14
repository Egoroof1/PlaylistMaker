package com.diego.playlistmaker.main.ui.root

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ActivityRootBinding
import com.diego.playlistmaker.services.MusicPlayerManager
import com.diego.playlistmaker.services.MusicService

class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding
    private var serviceConnection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootFragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        val line = binding.viewLine

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.playerFragment,
                R.id.addMediaPlayerFragment,
                R.id.playListListFragment,
                R.id.editPlayListFragment -> {
                    bottomNavigationView.isVisible = false
                    line.isVisible = false
                }
                else -> {
                    bottomNavigationView.isVisible = true
                    line.isVisible = true
                }
            }
        }

        bindToMusicService()

    }

    private fun bindToMusicService() {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicService.MusicBinder
                MusicPlayerManager.bindService(binder.getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                MusicPlayerManager.unbindService()
            }
        }
        serviceConnection = connection
        bindService(
            Intent(this, MusicService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onResume() {
        super.onResume()
        MusicPlayerManager.setAppForegroundState(true)
    }

    override fun onPause() {
        super.onPause()
        MusicPlayerManager.setAppForegroundState(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let { unbindService(it) }
        serviceConnection = null
    }
}