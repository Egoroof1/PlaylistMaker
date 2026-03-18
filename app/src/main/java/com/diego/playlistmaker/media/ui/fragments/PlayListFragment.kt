package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.diego.playlistmaker.databinding.FragmentPlayListBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.getValue

class PlayListFragment : Fragment() {

    private val args: PlayListFragmentArgs by navArgs()
    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!
    private var isFirstCreation: Boolean = false

    private var currentPlayList: PlayList? = null

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
        currentPlayList = args.playList

        Log.d("TAG", "onViewCreated: $currentPlayList")

        setBottomSheet()

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

    companion object {

    }
}