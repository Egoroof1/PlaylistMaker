package com.diego.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ActivityPlayerBinding
import com.diego.playlistmaker.search.domain.models.Track
import android.util.TypedValue

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.playerActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currentTrack = intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)

        if (currentTrack != null) {
            setupUI()
            setupObservers()
            viewModel.setTrack(currentTrack)
            viewModel.preparePlayer(currentTrack.previewUrl)
        } else {
            Toast.makeText(this, "Ошибка загрузки трека", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupUI() {
        // Настройка toolbar
        binding.toolbarPlayer.setNavigationOnClickListener { finish() }

        // Кнопка воспроизведения/паузы
        binding.btnPlayerPlay.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }

    private fun setupObservers() {
        // Наблюдаем за информацией о треке
        viewModel.trackInfo.observe(this) { trackInfo ->
            if (trackInfo != null) {
                updateTrackUI(trackInfo)
            }
        }

        // Наблюдаем за состоянием плеера
        viewModel.playerState.observe(this) { state ->
            updatePlayerUI(state)
        }

        // Наблюдаем за текущей позицией трека
        viewModel.currentPosition.observe(this) { position ->
            binding.trackCurrentTime.text = viewModel.getFormattedTime(position)
        }
    }

    private fun updateTrackUI(trackInfo: PlayerViewModel.TrackInfo) {
        // Загрузка изображения
        Glide.with(binding.image)
            .load(trackInfo.artworkUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(trackInfo.originalArtworkUrl)
            .transform(RoundedCorners(dpToPx(8f, binding.image)))
            .into(binding.image)

        // Установка текстовых полей
        binding.apply {
            playerTrackName.text = trackInfo.trackName
            playerArtistName.text = trackInfo.artistName
            trackTimeMillis.text = viewModel.getFormattedTime(trackInfo.trackTimeMillis.toInt())
            trackAlbumName.text = trackInfo.albumName
            playerTrackYear.text = trackInfo.releaseYear
            playerTrackGenre.text = trackInfo.genre
            playerTrackCountry.text = trackInfo.country
        }
    }

    private fun updatePlayerUI(state: PlayerViewModel.PlayerState) {
        when (state) {
            PlayerViewModel.PlayerState.DEFAULT -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                binding.trackCurrentTime.text = getString(R.string._00_00)
            }

            PlayerViewModel.PlayerState.PREPARING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
            }

            PlayerViewModel.PlayerState.PREPARED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.PLAYING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_pause)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.PAUSED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.ERROR -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                Toast.makeText(this, getString(R.string.replication_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dpToPx(dp: Float, context: View): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
    }
}