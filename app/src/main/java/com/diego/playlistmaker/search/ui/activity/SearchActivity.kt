package com.diego.playlistmaker.search.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.databinding.ActivitySearchBinding
import com.diego.playlistmaker.player.ui.PlayerActivity
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.presentation.TrackAdapter
import com.diego.playlistmaker.search.ui.view_model.SearchState
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var isClicked = false
    private var myHandler: Handler? = null
    private var currentEditText: String = CURRENT_TEXT

    private val viewModel: SearchViewModel by viewModel()

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()

    private val tracksAdapter by lazy {
        TrackAdapter(tracks) { track -> onTrackClicked(track) }
    }

    private val historyAdapter by lazy {
        TrackAdapter(historyTracks) { track -> onTrackClicked(track) }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()

        initViews()
        setupAdapters()
        setupClickListeners()
        setupTextWatcher()
        observeViewModel()

        // Восстанавливаем текст
        binding.editTextSearch.setText(currentEditText)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainSearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun initViews() {
        myHandler = Handler(Looper.getMainLooper())

        // Настройка кнопки назад
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAdapters() {
        binding.recyclerTracks.adapter = tracksAdapter
        binding.recyclerHistory.adapter = historyAdapter
    }

    private fun onTrackClicked(track: Track) {
        if (!isClicked) {
            isClicked = true
            myHandler?.postDelayed(
                { isClicked = false },
                ANTY_DOUBLE_CLICK
            )

            // Сохраняем в историю
            viewModel.saveTrackToHistory(track)

            // Переходим на PlayerActivity
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_EXTRA", track)
            }
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHistoryAdapter(newHistory: List<Track>) {
        historyTracks.clear()
        historyTracks.addAll(newHistory)
        binding.recyclerHistory.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupClickListeners() {
        // Очистка поля поиска
        binding.icClearEditText.setOnClickListener { clearSearch() }

        // Очистка истории поиска
        binding.btnClearHistory.setOnClickListener {
            historyTracks.clear()
            viewModel.clearHistory()

            // Сброс высоты RecyclerView
            binding.recyclerHistory.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            binding.recyclerHistory.requestLayout()

            binding.recyclerHistory.adapter?.notifyDataSetChanged()
            updateHistoryVisibility()
        }

        // Повторный поиск
        binding.btnErrorUpdate.setOnClickListener {
            performSearch()
        }

        // Кнопка "Готово" на клавиатуре
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE) {

                viewModel.cancelSearch()

                myHandler?.removeCallbacksAndMessages(null)

                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupTextWatcher() {

        binding.editTextSearch.doOnTextChanged {s,_,_,_ ->
            binding.icClearEditText.visibility = clearButtonVisibility(s)
            currentEditText = s?.toString() ?: ""

            val text = s?.toString() ?: ""
                viewModel.editTextChanged(text)
        }

    }

    private fun observeViewModel() {
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.ShowLoading -> {
                    showLoadingState()
                }
                is SearchState.HideSearchResults -> {
                    hideSearchResults()
                }
                is SearchState.ShowSearchResults -> {
                    showSearchResults()
                }
                is SearchState.ShowNotFound -> {
                    showNotFound()
                }
                is SearchState.HideHistory -> {
                    hideHistory()
                }
                is SearchState.ClearSearchResults -> {
                    clearSearchResults()
                }
                is SearchState.ClearHistory -> {
                    // История уже очищена во ViewModel
                }
                is SearchState.ShowHistory -> {
                    updateHistoryAdapter(state.tracks)
                    showHistory()
                }
                is SearchState.ShowSearchContent -> {
                    updateSearchResults(state.tracks)
                }
                is SearchState.ShowError -> {
                    showError(state.message)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSearchResults(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        binding.recyclerTracks.adapter?.notifyDataSetChanged()
    }

    private fun clearSearch() {
        binding.progressBar.isVisible = false
        binding.editTextSearch.text.clear()
        hideKeyboard()
        binding.editTextSearch.clearFocus()
        clearSearchResults()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearSearchResults() {
        tracks.clear()
        binding.recyclerTracks.adapter?.notifyDataSetChanged()
        binding.recyclerTracks.isVisible = false
        binding.searchErrorNotFound.isVisible = false
        binding.searchErrorNotSignal.isVisible = false
    }

    private fun performSearch() {
        hideKeyboard()
        binding.editTextSearch.clearFocus()

        if (binding.editTextSearch.text.isNotEmpty()) {
            val query = binding.editTextSearch.text.toString()
            viewModel.performSearch(query)
        }
    }

    private fun showLoadingState() {
        binding.apply {
            progressBar.isVisible = true
            searchHistory.isVisible = false
            recyclerTracks.isVisible = false
            searchErrorNotFound.isVisible = false
            searchErrorNotSignal.isVisible = false
        }
    }

    private fun showSearchResults() {
        binding.recyclerTracks.isVisible = true
    }

    private fun hideSearchResults() {
        binding.recyclerTracks.isVisible = false
    }

    private fun showNotFound() {
        binding.recyclerTracks.isVisible = false
        binding.searchErrorNotFound.isVisible = true
    }

    private fun showHistory() {
        if (historyTracks.isNotEmpty()) {
            binding.searchHistory.isVisible = true
        }
    }

    private fun hideHistory() {
        binding.searchHistory.isVisible = false
    }

    private fun updateHistoryVisibility() {
        binding.searchHistory.isVisible = historyTracks.isNotEmpty()
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        hideSearchResults()
        hideHistory()
        binding.searchErrorNotSignal.isVisible = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEditText = savedInstanceState.getString(KEY_CURRENT_TEXT, currentEditText)
        binding.editTextSearch.setText(currentEditText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TEXT, currentEditText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        myHandler?.removeCallbacksAndMessages(null)
        viewModel.cancelSearch()
    }

    companion object {
        const val CURRENT_TEXT = ""
        const val KEY_CURRENT_TEXT = "current_text"
        private const val ANTY_DOUBLE_CLICK = 500L
    }
}