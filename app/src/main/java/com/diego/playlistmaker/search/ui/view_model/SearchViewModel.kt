package com.diego.playlistmaker.search.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.models.UserRequestParam
import com.diego.playlistmaker.search.domain.use_case.ClearTrackHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.GetTracksHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.SaveTrackHistoryUseCase
import com.diego.playlistmaker.search.domain.use_case.SearchTracksWebUseCas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksWebUseCas,
    private val getTracksHistoryUseCase: GetTracksHistoryUseCase,
    private val saveTrackHistoryUseCase: SaveTrackHistoryUseCase,
    private val clearTrackHistoryUseCase: ClearTrackHistoryUseCase
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null
    private var lastSearchQuery = ""

    // Состояния
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val currentHistory = mutableListOf<Track>()

    init {
        loadHistory()
    }

    fun editTextChanged(text: String) {
        searchJob?.cancel()

        if (text.isEmpty()) {
            // Если текст пустой
            if (currentHistory.isNotEmpty()) {
                // Показываем историю если она не пустая
                _searchState.value = SearchState.ShowHistory(currentHistory)
            }
            // Скрываем результаты поиска
            _searchState.value = SearchState.HideSearchResults
            clearSearchResults()
        } else {
            // Если текст не пустой - запускаем поиск через debounce
            lastSearchQuery = text
            _isLoading.value = false // Сбрасываем loading при новом вводе

            searchJob = coroutineScope.launch {

                delay(SEARCH_DEBOUNCE_DELAY)

                performSearch(text)
            }
        }
    }

    fun performSearch(query: String) {

        Log.d("TAG", "performSearch: search go")
        if (query != lastSearchQuery) return

        _isLoading.value = true
        _searchState.value = SearchState.ShowLoading

        coroutineScope.launch(Dispatchers.IO) {
            try {

                val tracks = searchTracksUseCase.execute(UserRequestParam(query))

                coroutineScope.launch(Dispatchers.Main) {
                    _isLoading.value = false

                    if (query.isNotEmpty()) {
                        _searchState.value = SearchState.HideHistory
                        _searchState.value = SearchState.ShowSearchResults
                    }

                    if (tracks.isEmpty()) {
                        _searchState.value = SearchState.ShowNotFound
                    } else {
                        _searchState.value = SearchState.ShowSearchContent(tracks)
                    }
                }
            } catch (e: Exception) {
                coroutineScope.launch(Dispatchers.Main) {
                    _isLoading.value = false
                    _searchState.value = SearchState.HideSearchResults
                    _searchState.value = SearchState.HideHistory
                    _searchState.value = SearchState.ShowError(
                        when {
                            e.message?.contains("Network error") == true -> "Проверьте подключение к интернету"
                            e.message?.contains("Server error") == true -> "Ошибка сервера, попробуйте позже"
                            else -> "Произошла ошибка при поиске: ${e.message}"
                        }
                    )
                }
            }
        }
    }

    fun loadHistory() {
        coroutineScope.launch(Dispatchers.IO) {
            val history = getTracksHistoryUseCase.execute()
            currentHistory.clear()
            currentHistory.addAll(history)

            coroutineScope.launch(Dispatchers.Main) {
                updateHistoryVisibility()
            }
        }
    }

    private fun updateHistoryVisibility() {
        // Показываем историю если она не пустая
        if (currentHistory.isNotEmpty()) {
            _searchState.value = SearchState.ShowHistory(currentHistory)
        } else {
            _searchState.value = SearchState.HideHistory
        }
    }

    fun saveTrackToHistory(track: Track) {
        coroutineScope.launch(Dispatchers.IO) {
            saveTrackHistoryUseCase.execute(track)
            // Немедленно обновляем историю (как в оригинале)
            val updatedHistory = getTracksHistoryUseCase.execute()
            currentHistory.clear()
            currentHistory.addAll(updatedHistory)

            coroutineScope.launch(Dispatchers.Main) {
                updateHistoryVisibility()
            }
        }
    }

    fun clearHistory() {
        coroutineScope.launch(Dispatchers.IO) {
            clearTrackHistoryUseCase.execute()
            currentHistory.clear()

            coroutineScope.launch(Dispatchers.Main) {
                // Сброс высоты RecyclerView будет в активити
                _searchState.value = SearchState.ClearHistory
                updateHistoryVisibility()
            }
        }
    }

    fun clearSearchResults() {
        _searchState.value = SearchState.ClearSearchResults
    }

    fun cancelSearch() {
        searchJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}