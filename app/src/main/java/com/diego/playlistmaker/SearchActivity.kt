package com.diego.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.adapters.TrackAdapter
import com.diego.playlistmaker.models.Track
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private var currentEditText: String = CURRENT_TEXT


    private val tracks = mutableListOf(
        Track(
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTime = "5:01",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-" +
                    "2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTime = "4:35",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-" +
                    "71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTime = "4:10",
            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-" +
                    "8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTime = "5:33",
            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-" +
                    "2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTime = "5:03",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-" +
                    "03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Пример ну ооочень длинного названия",
            artistName = "Пример ну ооочень длинного названия",
            trackTime = "2:13",
            artworkUrl100 = ""
        )
    )

    companion object {
        const val CURRENT_TEXT = ""
        const val KEY_CURRENT_TEXT = "current_text"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnClear = findViewById<ImageView>(R.id.ic_clearEditText)
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)

        btnBack.setNavigationOnClickListener { finish() }

        btnClear.setOnClickListener {
            editTextSearch.text.clear()
            hideKeyboard()
            editTextSearch.clearFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //empty
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //empty
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                btnClear.visibility = clearButtonVisibility(s)
                currentEditText = s.toString()
            }
        }

        editTextSearch.addTextChangedListener(simpleTextWatcher)

        val recycler = findViewById<RecyclerView>(R.id.recycler_tracks)
        recycler.adapter = TrackAdapter(tracks)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEditText = savedInstanceState.getString(KEY_CURRENT_TEXT, currentEditText)
        findViewById<EditText>(R.id.editTextSearch).setText(currentEditText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TEXT, currentEditText)
    }

    fun clearButtonVisibility(s: CharSequence?): Int {
        return if(s.isNullOrEmpty()){
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun hideKeyboard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}