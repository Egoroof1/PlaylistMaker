package com.diego.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.HistoryRepositoryUseCase
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.domain.models.UserRequestParam
import com.diego.playlistmaker.search.domain.use_case.SearchTracksWebUseCas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksWebUseCas,
    private val historyRepository: HistoryRepositoryUseCase
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null
    private var lastSearchQuery = ""
    private var lastSearchResult = ""

    // Состояния

    private val _searchState = MutableLiveData(SearchScreenState())
    val searchState: LiveData<SearchScreenState> = _searchState

    init {
        loadHistory()
    }

    private fun updateState(updater: (SearchScreenState) -> SearchScreenState) {
        val currentState = _searchState.value ?: return
        _searchState.value = updater(currentState)
    }

    fun editTextChanged(text: String) {
        searchJob?.cancel()

        if (text.isEmpty()) {
            // Если текст пустой
            if (_searchState.value?.historyTracks?.isNotEmpty() ?: false) {
                // Показываем историю если она не пустая
                updateState { it.copy(userActions = UserActions.SHOW_HISTORY) }
            }
            // Скрываем результаты поиска
            updateState { it.copy(userActions = UserActions.HIDE_SEARCH_RESULT) }
            clearSearchResults()
        } else {
            if (text == lastSearchQuery) return

            lastSearchQuery = text

            searchJob = coroutineScope.launch {

                delay(SEARCH_DEBOUNCE_DELAY)

                lastSearchResult = lastSearchQuery

                performSearch(text)
            }
        }
    }

    fun performSearch(query: String) {

        updateState { it.copy(userActions = UserActions.SEARCH) }

        coroutineScope.launch(Dispatchers.IO) {
            try {

                val tracks = searchTracksUseCase.execute(UserRequestParam(query))

                coroutineScope.launch(Dispatchers.Main) {

                    if (tracks.isEmpty()) {
                        updateState { it.copy(userActions = UserActions.SHOW_NOT_FOUND) }
                    } else {
                        updateState { it.copy(searchTracks = tracks, userActions = UserActions.SHOW_SEARCH_RESULT) }
                    }
                }
            } catch (e: Exception) {
                coroutineScope.launch(Dispatchers.Main) {
                    updateState {
                        it.copy(userActions = UserActions.ERROR,
                            message = when {
                                e.message?.contains("Network error") == true -> "Проверьте подключение к интернету"
                                e.message?.contains("Server error") == true -> "Ошибка сервера, попробуйте позже"
                                else -> "Произошла ошибка при поиске: ${e.message}"
                            }
                        )
                    }
                }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            historyRepository.historyTracks().collect { entities ->
                updateState {
                    it.copy( historyTracks = entities )
                }
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {

            if (_searchState.value?.historyTracks?.contains(track) == true){
                historyRepository.deleteById(track.trackId)
                historyRepository.insertTrack(track)
                return@launch
            }

            if ((_searchState.value?.historyTracks?.size ?: 0) < 10){
                historyRepository.insertTrack(track)
            } else {
                historyRepository.deleteFirstTrack()
                historyRepository.insertTrack(track)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }

    fun clearSearchResults() {
        updateState { it.copy(searchTracks = emptyList()) }
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