package com.diego.playlistmaker.ui

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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.R
import com.diego.playlistmaker.Creator
import com.diego.playlistmaker.domain.models.Track
import com.diego.playlistmaker.domain.searchActv.use_case.ClearTrackHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.GetTracksHistoryUseCase
import com.diego.playlistmaker.domain.searchActv.use_case.SaveTrackHistoryUseCase
import com.diego.playlistmaker.domain.models.UserRequestParam
import com.diego.playlistmaker.domain.searchActv.use_case.SearchTracksWebUseCase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private var serverCode = 200

    private var isClicked = false

    private var myHandler: Handler? = null

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()
    private var currentEditText: String = CURRENT_TEXT

    //use_case
    private lateinit var getTracksHistoryUseCase: GetTracksHistoryUseCase
    private lateinit var saveTrackHistoryUseCase: SaveTrackHistoryUseCase
    private lateinit var clearTrackHistoryUseCase: ClearTrackHistoryUseCase
    private lateinit var searchTracksWebUseCase: SearchTracksWebUseCase

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

        // Инициализация use cases
        getTracksHistoryUseCase = Creator.provideGetTracksHistoryUseCase(this)
        saveTrackHistoryUseCase = Creator.provideSaveTrackHistoryUseCase(this)
        clearTrackHistoryUseCase = Creator.provideClearTrackHistoryUseCase(this)
        searchTracksWebUseCase = Creator.provideSearchTracksUseCase()

        initViews()
        setupAdapters()
        setupClickListeners()
        setupTextWatcher()
        setupHistory()

        Log.d("TAG", "onCreate: saved list history: ${getTracksHistoryUseCase.execute()}")
    }

    /**
     * Настраивает отступы для edge-to-edge режима (полноэкранный режим)
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Инициализирует все View элементы из layout
     */
    @SuppressLint("MissingInflatedId")
    private fun initViews() {
        // Настройка кнопки назад в toolbar
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

    /**
     * Настраивает адаптеры для RecyclerView и ограничение высоты истории
     */
    private fun setupAdapters() {
        recyclerTracks.adapter = TrackAdapter(tracks) { track ->
            onTrackClicked(track)
        }

        recyclerHistory.adapter = TrackAdapter(historyTracks) { track ->
            updateHistoryAdapter()
            onTrackClicked(track)
        }
    }

    /**
     * Обрабатывает клик по треку
     */
    private fun onTrackClicked(track: Track) {

        if (!isClicked){
            isClicked = true
            myHandler?.postDelayed(
                { isClicked = false },
                ANTY_DOUBLE_CLICK
            )

            //Сохраняем в историю
            saveTrackHistoryUseCase.execute(track)

            updateHistoryAdapter()


            // Переходим на PlayerActivity
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_EXTRA", track)
            }
            startActivity(intent)

            Log.d("TAG", "onTrackClicked: $track")

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHistoryAdapter() {
        val updatedHistory = getTracksHistoryUseCase.execute()
        historyTracks.clear()
        historyTracks.addAll(updatedHistory)
        recyclerHistory.adapter?.notifyDataSetChanged()
    }

    /**
     * Настраивает все обработчики кликов для кнопок и полей ввода
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setupClickListeners() {
        // Очистка поля поиска
        btnClear.setOnClickListener { clearSearch() }

        // Очистка истории поиска
        btnClearHistory.setOnClickListener {
            historyTracks.clear()
            clearTrackHistoryUseCase.execute()

            recyclerHistory.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

            recyclerHistory.adapter?.notifyDataSetChanged()
            updateHistoryVisibility()
        }

        // Повторный поиск при нажатии кнопки обновления
        btnUpdate.setOnClickListener {
            performSearch()
            Log.d("TAG", "onCreate: click")
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

    /**
     * Настраивает отслеживание изменений текста в поле поиска
     */
    private fun setupTextWatcher() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){
                // тут запускаем
                if (s.toString().isEmpty()){
                    Log.d("TAG", "++")
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
                                Log.d("TAG", "run: delay")

                                performSearch()
                            },
                            SEARCH_DEBOUNCE_DELAY
                        )
                    }


                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClear.visibility = clearButtonVisibility(s)
                currentEditText = s.toString()
                if (historyTracks.isEmpty()) {
                    showHistory()
                } else {
                    hideHistory()
                }
                Log.d("TAG", "onTextChanged: ")
            }
        })
    }

    /**
     * Инициализирует историю поиска
     */
    private fun setupHistory() {
        historyTracks.addAll(getTracksHistoryUseCase.execute())

        updateHistoryVisibility()
    }

    /**
     * Очищает поле поиска, скрывает клавиатуру и показывает историю
     */
    private fun clearSearch() {
        progressBar.isVisible = false
        editTextSearch.text.clear()
        hideKeyboard()
        editTextSearch.clearFocus()
        clearSearchResults()
    }

    /**
     * Очищает результаты поиска и скрывает все статусные сообщения
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun clearSearchResults() {
        tracks.clear()
        recyclerTracks.adapter?.notifyDataSetChanged()
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
    }

    /**
     * Выполняет поиск треков: скрывает клавиатуру и запускает запрос к API
     */
    private fun performSearch() {
        hideKeyboard()
        editTextSearch.clearFocus()

        if (editTextSearch.text.isNotEmpty()) {
            searchTracks(editTextSearch.text.toString())
        }
    }

    /**
     * Выполняет запрос к iTunes API для поиска треков по запросу
     * @param query Поисковый запрос
     */
    private fun searchTracks(query: String) {
        showLoadingState()

        lifecycleScope.launch {
            try {
                val tracks = searchTracksWebUseCase.execute(UserRequestParam(query))
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

    /**
     * Обрабатывает результат поиска
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchResult(tracks: List<Track>) {
        if (editTextSearch.text.isNotEmpty()) {
            hideHistory()
            showSearchResults()
        }

        progressBar.isVisible = false

        this.tracks.clear()

        if (tracks.isNotEmpty()) {
            this.tracks.addAll(tracks)
            recyclerTracks.adapter?.notifyDataSetChanged()

            Log.d("TAG", "Search results: ${tracks.size} tracks")
        } else {
            showNotFound()
            Log.d("TAG", "handleSearchResult: else")
        }
    }

    /**
     * Обрабатывает ошибку при запросе к API
     */
    private fun handleErrorResponse(errorMessage: String) {
        progressBar.isVisible = false
        hideSearchResults()
        hideHistory()
        statusNotSignal.isVisible = true
        // Можно показать Toast с ошибкой
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.d("TAG", "Search error: $errorMessage")
    }

    /**
     * Показывает состояние загрузки (скрывает все остальные элементы)
     */
    private fun showLoadingState() {
        progressBar.isVisible = true
        searchHistory.isVisible = false
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = false
        statusNotSignal.isVisible = false
    }

    /**
     * Показывает результаты поиска
     */
    private fun showSearchResults() {
        recyclerTracks.isVisible = true
    }

    /**
     * Скрывает результаты поиска
     */
    private fun hideSearchResults() {
        recyclerTracks.isVisible = false
    }

    /**
     * Показывает сообщение "Ничего не найдено"
     */
    private fun showNotFound() {
        recyclerTracks.isVisible = false
        statusNotFound.isVisible = true
        Log.d("TAG", "onResponse: пусто")
    }

    /**
     * Показывает историю поиска
     */
    private fun showHistory() {
        if (historyTracks.isNotEmpty()) {
            searchHistory.isVisible = true
        }
    }

    /**
     * Скрывает историю поиска
     */
    private fun hideHistory() {
        searchHistory.isVisible = false
    }

    /**
     * Обновляет видимость истории поиска в зависимости от наличия данных
     */
    private fun updateHistoryVisibility() {
        searchHistory.isVisible = historyTracks.isNotEmpty()
    }

    /**
     * Восстанавливает состояние активности после поворота экрана
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentEditText = savedInstanceState.getString(KEY_CURRENT_TEXT, currentEditText)
        editTextSearch.setText(currentEditText)
    }

    /**
     * Сохраняет состояние активности перед поворотом экрана или уничтожением
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TEXT, currentEditText)
    }

    /**
     * Определяет видимость кнопки очистки поля поиска
     * @param s Текст из поля поиска
     * @return Видимость кнопки (VISIBLE или GONE)
     */
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /**
     * Скрывает клавиатуру
     */
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