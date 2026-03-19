package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayListBinding
import com.diego.playlistmaker.media.ui.state.PlayListState
import com.diego.playlistmaker.media.ui.view_model.PlayListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayListFragment : Fragment() {

    private val args: PlayListFragmentArgs by navArgs()
    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayListViewModel by viewModel()
    private var isFirstCreation: Boolean = false

    private var currentPlayListId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPlayListId = args.playListId

        observeViewModel()
        setBottomSheet()
    }

    private fun observeViewModel(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPlayListBtId(currentPlayListId)

            viewModel.state.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun setBottomSheet(){
        val bottomSheetContainer = binding.playlistBottomSheet
        val overlay = binding.overlay

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            //запрещаем скрытие
            isHideable = false
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.isVisible = false
                    }

                    else -> {
                        overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })


        if (!isFirstCreation) {
            // Первый вход - скрываем bottom sheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            isFirstCreation = true
            overlay.isVisible = true
            overlay.alpha = 0.0f
        } else {
            // При возвращении - синхронизируем overlay с текущим состоянием behavior
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    overlay.isVisible = false
                }
                else -> {
                    overlay.isVisible = true
                    overlay.alpha = 0.0f
                }
            }
        }
    }

    private fun updateUi(state: PlayListState){
        if (_binding == null) return

        val playList = state.playList
        if (playList == null) {
            findNavController().popBackStack()
            Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            return
        }
        with(binding){
            Glide.with(ivCaverPlaylist.context)
                .load(playList.coverImagePath)
                .placeholder(R.drawable.placeholder) // показываем, пока грузится
                .error(R.drawable.placeholder) // если ошибка загрузки
                .centerCrop()
                .into(ivCaverPlaylist)

            tvNamePlaylist.text = playList.name
            tvYearPlaylist.text = "202020"
            tvTotalTimePlaylist.text = "${playList.totalTimeMillis/1000/60} минут"
            tvQuantityTracksPlaylist.text = getTracksCountText(playList.quantityTracks)
        }
    }

    private fun getTracksCountText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count трек"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "$count трека"
            else -> "$count треков"
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}