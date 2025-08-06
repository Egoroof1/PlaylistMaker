package com.diego.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val main = findViewById<ConstraintLayout>(R.id.main)

        main.setOnClickListener {
            finish()
        }
    }
}