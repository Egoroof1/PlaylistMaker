package com.diego.playlistmaker

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.services.MyShared
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val image = findViewById<ImageView>(R.id.image)
        val trackName = findViewById<TextView>(R.id.player_track_name)
        val artistName = findViewById<TextView>(R.id.player_artist_name)
        val trackCurrentTime = findViewById<TextView>(R.id.player_track_time)
        val trackTimeMillis = findViewById<TextView>(R.id.player_track_time2)
        val trackAlbumName = findViewById<TextView>(R.id.player_track_album_name)
        val trackYear = findViewById<TextView>(R.id.player_track_year)
        val trackGenre = findViewById<TextView>(R.id.player_track_genre)
        val trackCountry = findViewById<TextView>(R.id.player_track_country)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_player)
        toolbar.setNavigationOnClickListener { finish() }

        val currentTrack = MyShared.getCurrentTrack()

        if (currentTrack != null) {
            Glide.with(image)
                .load(currentTrack.artworkUrl100.replaceAfterLast("/", "512x512.jpg"))
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(currentTrack.artworkUrl100) //Обработка отсутствия качества 512
                .transform(RoundedCorners(dpToPx(8f, image)))
                .into(image)

            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackCurrentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis)
            trackTimeMillis.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis)
            trackAlbumName.text = currentTrack.collectionName
            trackYear.text = currentTrack.releaseDate.split("-")[0]
            trackGenre.text = currentTrack.primaryGenreName
            trackCountry.text = currentTrack.country
        } else {
            Toast.makeText(this, "Ошибка загрузки трека", Toast.LENGTH_SHORT).show()
            finish()
        }


    }
    private fun dpToPx(dp: Float, context: View): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}