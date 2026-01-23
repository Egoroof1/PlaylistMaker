package com.diego.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diego.playlistmaker.databinding.FragmentSearchBinding
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.presentation.TrackAdapter
import com.diego.playlistmaker.search.ui.view_model.SearchState
import com.diego.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
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

    // view ещё не создана
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        if (savedInstanceState != null) {
            currentEditText = savedInstanceState.getString(KEY_CURRENT_TEXT, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupAdapters()
        setupClickListeners()
        setupTextWatcher()
        observeViewModel()
    }

    @SuppressLint("MissingInflatedId")
    private fun initViews() {
        myHandler = Handler(Looper.getMainLooper())
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

            // Переходим на PlayerFragment
            val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
            findNavController().navigate(action)
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
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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

        binding.icClearEditText.isEnabled = true
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

            icClearEditText.isEnabled = false
        }
    }

    private fun showSearchResults() {
        binding.recyclerTracks.isVisible = true

        binding.icClearEditText.isEnabled = true
    }

    private fun hideSearchResults() {
        binding.recyclerTracks.isVisible = false
    }

    private fun showNotFound() {
        binding.recyclerTracks.isVisible = false
        binding.searchErrorNotFound.isVisible = true

        binding.icClearEditText.isEnabled = true
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

        binding.icClearEditText.isEnabled = true

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
        val view = activity?.currentFocus ?: view
        view?.let {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentEditText.isNotEmpty()) {
            viewModel.performSearch(currentEditText)
        }

        // Обновляем историю
        viewModel.loadHistory()
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