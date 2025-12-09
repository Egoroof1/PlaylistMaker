package com.diego.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.R
import com.diego.playlistmaker.presentation.ui.adapters.TrackAdapter
import com.diego.playlistmaker.creator.Creator
import com.diego.playlistmaker.domain.entity.SearchResult
import com.diego.playlistmaker.domain.entity.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    // Use Cases
    private val getTracksListWebUseCase = Creator.provideGetTracksListWebUseCase()
    private val getTracksListLocalUseCase = Creator.provideGetTracksListLocalUseCase()
    private val saveTrackToHistoryUseCase = Creator.provideSaveTrackToHistoryUseCase()
    private val clearHistoryUseCase = Creator.provideClearHistoryUseCase()

    private var isClicked = false
    private var myHandler: Handler? = null

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()
    private var currentEditText: String = CURRENT_TEXT

    // Views
    private lateinit var progressBar: ProgressBar
    private lateinit var editTextSearch: EditText
    private lateinit var btnClear: ImageView
    private lateinit var recyclerTracks: RecyclerView
    private lateinit var statusNotFound: LinearLayout
    private lateinit var statusNotSignal: LinearLayout
    private lateinit var searchHistory: LinearLayout
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var btnUpdate: MaterialButton
    private lateinit var btnClearHistory: MaterialButton

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        setupWindowInsets()
        initViews()
        setupAdapters()
        setupClickListeners()
        setupTextWatcher()
        setupHistory()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun initViews() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        myHandler = Handler(Looper.getMainLooper())

        editTextSearch = findViewById(R.id.editTextSearch)
        btnClear = findViewById(R.id.ic_clearEditText)
        recyclerTracks = findViewById(R.id.recycler_tracks)
        statusNotFound = findViewById(R.id.search_error_not_found)
        statusNotSignal = findViewById(R.id.search_error_not_signal)
        btnUpdate = findViewById(R.id.btn_error_update)
        searchHistory = findViewById(R.id.search_history)
        btnClearHistory = findViewById(R.id.btn_clear_history)
        recyclerHistory = findViewById(R.id.recycler_history)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupAdapters() {
        recyclerTracks.adapter = TrackAdapter(tracks) { track ->
            onTrackClicked(track)
        }
        recyclerHistory.adapter = TrackAdapter(historyTracks) { track ->
            onTrackClicked(track)
        }
    }

    private fun onTrackClicked(track: Track) {
        if (!isClicked) {
            isClicked = true
            myHandler?.postDelayed({ isClicked = false }, ANTY_DOUBLE_CLICK)

            // Используем Use Case для сохранения
            saveTrackToHistoryUseCase.execute(track)
            setupHistory() // Обновляем историю

            recyclerHistory.adapter?.notifyItemRangeChanged(0, historyTracks.size)

            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_EXTRA", track)
            }
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupClickListeners() {
        btnClear.setOnClickListener { clearSearch() }

        btnClearHistory.setOnClickListener {
            // Используем Use Case для очистки
            clearHistoryUseCase.execute()
            historyTracks.clear()
            updateHistoryVisibility()
            recyclerHistory.adapter?.notifyDataSetChanged()
        }

        btnUpdate.setOnClickListener {
            performSearch()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                myHandler?.removeCallbacksAndMessages(null)
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupTextWatcher() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myHandler?.removeCallbacksAndMessages(null)

                if (s.isNullOrEmpty()) {
                    if (historyTracks.isNotEmpty()) showHistory()
                    hideSearchResults()
                    clearSearchResults()
                } else {
                    hideHistory()
                    myHandler?.postDelayed({ performSearch() }, SEARCH_DEBOUNCE_DELAY)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClear.visibility = clearButtonVisibility(s)
                currentEditText = s.toString()
                if (s.isNullOrEmpty() && historyTracks.isNotEmpty()) {
                    showHistory()
                } else {
                    hideHistory()
                }
            }
        })
    }

    private fun setupHistory() {
        historyTracks.clear()
        // Используем Use Case для получения истории
        historyTracks.addAll(getTracksListLocalUseCase.execute())
        updateHistoryVisibility()
    }

    private fun clearSearch() {
        editTextSearch.text.clear()
        hideKeyboard()
        editTextSearch.clearFocus()
        clearSearchResults()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearSearchResults() {
        tracks.clear()
        recyclerTracks.adapter?.notifyDataSetChanged()
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
    }

    private fun performSearch() {
        val query = editTextSearch.text.toString().trim()
        if (query.isEmpty()) return

        hideKeyboard()
        editTextSearch.clearFocus()
        searchTracks(query)
    }

    private fun searchTracks(query: String) {
        showLoadingState()

        // Используем GetTracksListWebUseCase для поиска
        getTracksListWebUseCase.execute(query) { result ->
            runOnUiThread {
                when (result) {
                    is SearchResult.Success -> handleSuccessResponse(result.tracks)
                    is SearchResult.NotFound -> showNotFound()
                    is SearchResult.NetworkError -> handleErrorResponse()
                }
            }
        }
    }

    /**
     * Обрабатывает успешный ответ от API
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleSuccessResponse(results: List<Track>) {
        hideHistory()
        showSearchResults()
        progressBar.isVisible = false

        tracks.clear()
        tracks.addAll(results)
        recyclerTracks.adapter?.notifyDataSetChanged()
        Log.d("TAG", "handleSuccessResponse: найдено ${results.size} треков")
    }

    /**
     * Обрабатывает ошибку при запросе к API (сетевая или серверная ошибка)
     */
    private fun handleErrorResponse() {
        progressBar.isVisible = false
        hideSearchResults()
        hideHistory()
        statusNotSignal.isVisible = true
        Log.d("TAG", "handleErrorResponse: Ошибка сети или сервера")
    }

    private fun showLoadingState() {
        progressBar.isVisible = true
        searchHistory.isVisible = false
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
    }

    private fun showSearchResults() {
        recyclerTracks.isVisible = true
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
    }

    private fun hideSearchResults() {
        recyclerTracks.isVisible = false
    }

    /**
     * Показывает сообщение "Ничего не найдено"
     */
    private fun showNotFound() {
        progressBar.isVisible = false
        hideHistory()
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = true
        statusNotSignal.isVisible = false
        Log.d("TAG", "showNotFound: ничего не найдено")
    }

    private fun showHistory() {
        searchHistory.isVisible = true
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
        progressBar.isVisible = false
    }

    private fun hideHistory() {
        searchHistory.isVisible = false
    }

    private fun updateHistoryVisibility() {
        searchHistory.isVisible = historyTracks.isNotEmpty()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEditText = savedInstanceState.getString(KEY_CURRENT_TEXT, currentEditText)
        editTextSearch.setText(currentEditText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TEXT, currentEditText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        myHandler?.removeCallbacksAndMessages(null)
    }

    companion object {
        const val CURRENT_TEXT = ""
        const val KEY_CURRENT_TEXT = "current_text"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val ANTY_DOUBLE_CLICK = 500L
    }
}