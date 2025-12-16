package com.diego.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.ActivityPlayerBinding
import com.diego.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private var handler: Handler? = null

    private var trackTimer: Int = 0

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

        handler = Handler(Looper.getMainLooper())

        val currentTrack = intent.getParcelableExtra("TRACK_EXTRA", Track::class.java)

        if (currentTrack != null) {
            setupPlayerUI(currentTrack)
            preparePlayer(currentTrack)
        } else {
            Toast.makeText(this, "Ошибка загрузки трека", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupPlayerUI(currentTrack: Track) {

        trackTimer = mediaPlayer.currentPosition

        // Настройка toolbar
        findViewById<MaterialToolbar>(R.id.toolbar_player).setNavigationOnClickListener { finish() }

        // Заполнение данных
        Glide.with(binding.image)
            .load(currentTrack.artworkUrl100.replaceAfterLast("/", "512x512.jpg"))
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(currentTrack.artworkUrl100)
            .transform(RoundedCorners(dpToPx(8f, binding.image)))
            .into(binding.image)

            binding.apply{
                playerTrackName.text = currentTrack.trackName
                playerArtistName.text = currentTrack.artistName
                trackCurrentTime.text = getTrackTimer(trackTimer)
                trackTimeMillis.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(currentTrack.trackTimeMillis)
                trackAlbumName.text = currentTrack.collectionName
                val dateSplit = currentTrack.releaseDate.split("-")
                playerTrackYear.text = dateSplit.firstOrNull() ?: ""
                playerTrackGenre.text = currentTrack.primaryGenreName
                playerTrackCountry.text = currentTrack.country
            }

        binding.btnPlayerPlay.setOnClickListener {
            when (playerState) {
                STATE_PLAYING -> {
                    pausePlayer()
                }

                STATE_PREPARED, STATE_PAUSED -> {
                    startPlayer()
                }
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

    private fun preparePlayer(currentTrack: Track) {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            Log.d("TAG", "preparePlayer: reade")
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
            binding.trackCurrentTime.text = getString(R.string._00_00)
            trackTimer = 0
        }
    }

    private fun startTimer(){

        handler?.post(
            createUpdateTimerTrack()
        )
    }

    private fun createUpdateTimerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    // Если всё ещё отсчитываем секунды —
                    // обновляем UI и снова планируем задачу
                    val currentPositionTrackToMillis = LocalTime.ofSecondOfDay((mediaPlayer.currentPosition/1000).toLong())
                        .format(DateTimeFormatter.ofPattern("mm:ss"))
                    binding.trackCurrentTime.text = currentPositionTrackToMillis

                    handler?.postDelayed(this, 500L)
                }
            }

        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_pause)
        playerState = STATE_PLAYING

        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
        playerState = STATE_PAUSED
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun getTrackTimer(time: Int): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}