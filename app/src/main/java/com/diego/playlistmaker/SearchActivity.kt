package com.diego.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.adapters.TrackAdapter
import com.diego.playlistmaker.models.Track
import com.diego.playlistmaker.models.TrackResponse
import com.diego.playlistmaker.services.ITunesApi
import com.diego.playlistmaker.services.MyShared
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val CURRENT_TEXT = ""
        const val KEY_CURRENT_TEXT = "current_text"
        private const val BASE_URL = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()
    private var currentEditText: String = CURRENT_TEXT

    // Views
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

        Log.d("TAG", "onCreate: saved list history: $historyTracks")
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

        editTextSearch = findViewById(R.id.editTextSearch)
        btnClear = findViewById(R.id.ic_clearEditText)
        recyclerTracks = findViewById(R.id.recycler_tracks)
        statusNotFound = findViewById(R.id.search_error_not_found)
        statusNotSignal = findViewById(R.id.search_error_not_signal)
        btnUpdate = findViewById(R.id.btn_error_update)
        searchHistory = findViewById(R.id.search_history)
        btnClearHistory = findViewById(R.id.btn_clear_history)
        recyclerHistory = findViewById(R.id.recycler_history)
    }

    /**
     * Настраивает адаптеры для RecyclerView и ограничение высоты истории
     */
    private fun setupAdapters() {
        recyclerTracks.adapter = TrackAdapter(tracks) { track ->
            onTrackClicked(track)
        }
        recyclerHistory.adapter = TrackAdapter(historyTracks) {track ->
            onTrackClicked(track)
        }
    }

    /**
     * Обрабатывает клик по треку
     */
    private fun onTrackClicked(track: Track) {
        Log.d("TAG", "onTrackClicked: $track")

        //Сохраняем в историю
        savaToHistory(track)

        // Переходим на PlayerActivity
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("TRACK_EXTRA", track)
        }
        startActivity(intent)
//        MyShared.saveCurrentTrack(track)
//        Log.d("TAG", "onTrackClicked: Track is saved: ${MyShared.getCurrentTrack()}")
//        startActivity(Intent(this, PlayerActivity::class.java))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun savaToHistory(track: Track){
        if (historyTracks.contains(track)) {
            historyTracks.remove(track)
        }

        if (historyTracks.size >= 10){
            historyTracks.removeAt(historyTracks.lastIndex)
        }

        historyTracks.add(0, track)
        MyShared.saveHistory(historyTracks)

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
            MyShared.clearHistory()

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
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClear.visibility = clearButtonVisibility(s)
                currentEditText = s.toString()
                if (!historyTracks.isNullOrEmpty()) {
                    showHistory()
                } else {
                    hideHistory()
                }
            }
        })
    }

    /**
     * Инициализирует историю поиска (заглушка для демонстрации)
     */
    private fun setupHistory() {
        historyTracks.addAll(MyShared.getHistory())

        updateHistoryVisibility()
    }

    /**
     * Очищает поле поиска, скрывает клавиатуру и показывает историю
     */
    private fun clearSearch() {
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

        iTunesApi.searchSongs(query).enqueue(object : Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    handleSuccessResponse(response)
                } else {
                    handleErrorResponse()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                handleErrorResponse()
                Log.d("TAG", "onFailure: Сетевая ошибка")
            }
        })
    }

    /**
     * Обрабатывает успешный ответ от API
     * @param response Ответ от сервера с данными треков
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleSuccessResponse(response: Response<TrackResponse>) {
        hideHistory()
        showSearchResults()

        tracks.clear()

        response.body()?.results?.let { results ->
            if (results.isNotEmpty()) {
                tracks.addAll(results)
                recyclerTracks.adapter?.notifyDataSetChanged()
                Log.d("TAG", "onResponse: how many results: ${response.body()?.resultCount}")
            } else {
                showNotFound()
            }
        } ?: showNotFound()
    }

    /**
     * Обрабатывает ошибку при запросе к API (сетевая или серверная ошибка)
     */
    private fun handleErrorResponse() {
        hideSearchResults()
        hideHistory()
        statusNotSignal.isVisible = true
        Log.d("TAG", "onResponse: Серверная ошибка не 200-ОК")
    }

    /**
     * Показывает состояние загрузки (скрывает все остальные элементы)
     */
    private fun showLoadingState() {
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
        searchHistory.isVisible = true
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
}