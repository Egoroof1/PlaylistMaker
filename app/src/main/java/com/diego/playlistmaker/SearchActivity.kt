package com.diego.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.adapters.TrackAdapter
import com.diego.playlistmaker.models.Track
import com.diego.playlistmaker.models.TrackResponse
import com.diego.playlistmaker.services.ITunesApi
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    var actionID: Int = 0

    // создание Retrofit клиента
    private val imdBaseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(imdBaseURL)
        .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON -> Kotlin объекты
        .build()

    private val iTunesApi = retrofit.create(ITunesApi::class.java) // Создание сервиса
    //------------

    private val tracks = mutableListOf<Track>()
    private var currentEditText: String = CURRENT_TEXT


//    private val tracks = mutableListOf(
//        Track(
//            trackName = "Smells Like Teen Spirit",
//            artistName = "Nirvana",
//            trackTime = "5:01",
//            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-" +
//                    "2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
//        ),
//        Track(
//            trackName = "Billie Jean",
//            artistName = "Michael Jackson",
//            trackTime = "4:35",
//            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-" +
//                    "71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
//        ),
//        Track(
//            trackName = "Stayin' Alive",
//            artistName = "Bee Gees",
//            trackTime = "4:10",
//            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-" +
//                    "8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
//        ),
//        Track(
//            trackName = "Whole Lotta Love",
//            artistName = "Led Zeppelin",
//            trackTime = "5:33",
//            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-" +
//                    "2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
//        ),
//        Track(
//            trackName = "Sweet Child O'Mine",
//            artistName = "Guns N' Roses",
//            trackTime = "5:03",
//            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-" +
//                    "03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
//        ),
//        Track(
//            trackName = "Пример ну ооочень длинного названия",
//            artistName = "Пример ну ооочень длинного названия",
//            trackTime = "2:13",
//            artworkUrl100 = ""
//        )
//    )

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

        val statusNotFound = findViewById<LinearLayout>(R.id.search_error_not_found)
        val statusNotSignal = findViewById<LinearLayout>(R.id.search_error_not_signal)
        val btnUpdate = findViewById<MaterialButton>(R.id.btn_error_update)

        // Обработчик клавиатуры (кнопка "Готово")
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            this.actionID = actionId
            requestServer(
                editTextSearch,
                recycler,
                statusNotFound,
                statusNotSignal,
                actionID
            )
        }

        // кнопка обновления
        btnUpdate.setOnClickListener {
            requestServer(editTextSearch, recycler, statusNotFound, statusNotSignal, actionID)
            Log.d("TAG", "onCreate: click")
        }

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
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun requestServer(
        editTextSearch: EditText,
        recycler: RecyclerView,
        statusNotFound: LinearLayout,
        statusNotSignal: LinearLayout,
        actionId: Int
    ): Boolean {

        Log.d("TAG", "requestServer: зашли в метод")

        Log.d("TAG", "requestServer: начало запроса")

        recycler.visibility = View.VISIBLE
        statusNotFound.visibility = View.GONE
        statusNotSignal.visibility = View.GONE

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()     // Скрыть клавиатуру
            editTextSearch.clearFocus()       // Убрать фокус с поля ввода

            if (editTextSearch.text.isNotEmpty()) {
                // Вызов API при нажатии кнопки
                iTunesApi.searchSongs(editTextSearch.text.toString()).enqueue(object :
                    Callback<TrackResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) { // Если успешный ответ
                            tracks.clear() // Очистка старого списка

                            // Добавление новых данных
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                recycler.adapter?.notifyDataSetChanged() // Обновление списка
                            }

                            Log.d(
                                "TAG",
                                "onResponse: how many results: ${response.body()?.resultCount}"
                            )

                            // Показать/скрыть сообщение в зависимости от результатов
                            if (tracks.isEmpty()) {
                                Log.d("TAG", "onResponse: пусто")

                                recycler.visibility = View.GONE
                                statusNotFound.visibility = View.VISIBLE
                            }
                        } else {
                            // Обработка ошибки HTTP

                            recycler.visibility = View.GONE
                            statusNotSignal.visibility = View.VISIBLE
                            Log.d("TAG", "onResponse: Серверная ошибка не 200-ОК")
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        // Обработка сетевой ошибки

                        recycler.visibility = View.GONE
                        statusNotSignal.visibility = View.VISIBLE

                        Log.d("TAG", "onFailure: Сетевая ошибка")
                    }
                })
            }

            return true
        }

        Log.d("TAG", "requestServer: вышли из метода")
        return false
    }
}