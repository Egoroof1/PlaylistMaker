package com.diego.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMediaLibrary = findViewById<Button>(R.id.btn_media_library)
        val btnSettings = findViewById<Button>(R.id.btn_settings)


        val clickForObject = object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }
        btnSearch.setOnClickListener(clickForObject)

        /*разблочить после сдачи*/
//        btnSearch.setOnClickListener{
//            startActivity(Intent(this, SearchActivity::class.java))
//        }

        btnMediaLibrary.setOnClickListener {

            startActivity(Intent(this, MediaActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}