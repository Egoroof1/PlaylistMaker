package com.diego.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.creator.Creator
import com.diego.playlistmaker.databinding.ActivitySearchBinding
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.models.UserRequestParam
import com.diego.playlistmaker.search.domain.use_case.ClearTrackHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.GetTracksHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.SaveTrackHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.SearchTracksWebUseCas
import com.diego.playlistmaker.player.ui.PlayerActivity
import com.diego.playlistmaker.search.presentation.TrackAdapter
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var serverCode = 200

    private var isClicked = false

    private var myHandler: Handler? = null

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()
    private var currentEditText: String = CURRENT_TEXT

    //use_case
    private lateinit var getTracksHistoryUseCaseImpl: GetTracksHistoryUseCase
    private lateinit var saveTrackHistoryUseCaseImpl: SaveTrackHistoryUseCase
    private lateinit var clearTrackHistoryUseCaseImpl: ClearTrackHistoryUseCase
    private lateinit var searchTracksWebUseCaseImpl: SearchTracksWebUseCas

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()

        initUseCase()
        initViews()
        setupAdapters()
        setupClickListeners()
        setupTextWatcher()
        setupHistory()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainSearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUseCase(){
        // Инициализация use cases
        getTracksHistoryUseCaseImpl = Creator.provideGetTracksHistoryUseCase()
        saveTrackHistoryUseCaseImpl = Creator.provideSaveTrackHistoryUseCase()
        clearTrackHistoryUseCaseImpl = Creator.provideClearTrackHistoryUseCase()
        searchTracksWebUseCaseImpl = Creator.provideSearchTracksUseCase()
    }

    @SuppressLint("MissingInflatedId")
    private fun initViews() {
        myHandler = Handler(Looper.getMainLooper())
    }

    private fun setupAdapters() {
        binding.apply {
            recyclerTracks.adapter = TrackAdapter(tracks) { track ->
                onTrackClicked(track)
            }

            recyclerHistory.adapter = TrackAdapter(historyTracks) { track ->
                updateHistoryAdapter()
                onTrackClicked(track)
            }
        }
    }

    private fun onTrackClicked(track: Track) {

        if (!isClicked){
            isClicked = true
            myHandler?.postDelayed(
                { isClicked = false },
                ANTY_DOUBLE_CLICK
            )

            //Сохраняем в историю
            saveTrackHistoryUseCaseImpl.execute(track)

            updateHistoryAdapter()

            // Переходим на PlayerActivity
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_EXTRA", track)
            }
            startActivity(intent)

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHistoryAdapter() {
        val updatedHistory = getTracksHistoryUseCaseImpl.execute()
        historyTracks.clear()
        historyTracks.addAll(updatedHistory)
        binding.recyclerHistory.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupClickListeners() {
        binding.apply{
            // Настройка кнопки назад в toolbar
            toolbar.setNavigationOnClickListener { finish() }

            // Очистка поля поиска
            icClearEditText.setOnClickListener { clearSearch() }

            // Очистка истории поиска
            btnClearHistory.setOnClickListener {
                historyTracks.clear()
                clearTrackHistoryUseCaseImpl.execute()

                recyclerHistory.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

                recyclerHistory.adapter?.notifyDataSetChanged()
                updateHistoryVisibility()
            }

            // Повторный поиск при нажатии кнопки обновления
            btnErrorUpdate.setOnClickListener {
                performSearch()
            }

            // Обработка нажатия кнопки "Готово" на клавиатуре
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
    }

    private fun setupTextWatcher() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){
                // тут запускаем
                if (s.toString().isEmpty()){
                    //тут нужно удалить задачу из списка
                    myHandler?.removeCallbacksAndMessages(null)

                    if (historyTracks.isNotEmpty()) {
                        showHistory()
                    }
                    hideSearchResults()
                    clearSearchResults()
                } else {
                    // тут запускаем новый поток
                    myHandler?.removeCallbacksAndMessages(null)

                    if (serverCode == 200){
                        myHandler?.postDelayed(
                            {
                                performSearch()
                            },
                            SEARCH_DEBOUNCE_DELAY
                        )
                    }


                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.icClearEditText.visibility = clearButtonVisibility(s)
                currentEditText = s.toString()
                if (historyTracks.isEmpty()) {
                    showHistory()
                } else {
                    hideHistory()
                }
            }
        })
    }

    private fun setupHistory() {
        historyTracks.addAll(getTracksHistoryUseCaseImpl.execute())

        updateHistoryVisibility()
    }

    private fun clearSearch() {
        binding.apply{
            progressBar.isVisible = false
            editTextSearch.text.clear()
            hideKeyboard()
            editTextSearch.clearFocus()
            clearSearchResults()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearSearchResults() {
        tracks.clear()
        binding.apply{
            recyclerTracks.adapter?.notifyDataSetChanged()
            recyclerTracks.isVisible = false
            searchErrorNotFound.isVisible = false
            searchErrorNotSignal.isVisible = false
        }
    }

    private fun performSearch() {
        hideKeyboard()
        binding.apply{
            editTextSearch.clearFocus()

            if (editTextSearch.text.isNotEmpty()) {
                searchTracks(editTextSearch.text.toString())
            }
        }
    }

    private fun searchTracks(query: String) {
        showLoadingState()

        lifecycleScope.launch {
            try {
                val tracks = searchTracksWebUseCaseImpl.execute(UserRequestParam(query))
                handleSearchResult(tracks)
            } catch (e: Exception) {
                // Обрабатываем разные типы ошибок
                val errorMessage = when {
                    e.message?.contains("Network error") == true -> "Проверьте подключение к интернету"
                    e.message?.contains("Server error") == true -> "Ошибка сервера, попробуйте позже"
                    else -> "Произошла ошибка при поиске: ${e.message}"
                }
                handleErrorResponse(errorMessage)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchResult(tracks: List<Track>) {
        if (binding.editTextSearch.text.isNotEmpty()) {
            hideHistory()
            showSearchResults()
        }

        binding.progressBar.isVisible = false

        this.tracks.clear()

        if (tracks.isNotEmpty()) {
            this.tracks.addAll(tracks)
            binding.recyclerTracks.adapter?.notifyDataSetChanged()
        } else {
            showNotFound()
        }
    }

    private fun handleErrorResponse(errorMessage: String) {
        binding.progressBar.isVisible = false
        hideSearchResults()
        hideHistory()
        binding.searchErrorNotSignal.isVisible = true
        // Показать Toast с ошибкой
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingState() {
        binding.apply{
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
    }

    companion object {
        const val CURRENT_TEXT = ""
        const val KEY_CURRENT_TEXT = "current_text"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val ANTY_DOUBLE_CLICK = 500L
    }
}