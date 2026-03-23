package com.diego.playlistmaker.media.ui.fragments

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentAddMediaPlayerBinding
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import com.diego.playlistmaker.media.ui.view_model.AddMediaPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddMediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentAddMediaPlayerBinding

    private val viewModel: AddMediaPlayerViewModel by viewModel()

    private var currentUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.pickerImage.setImageURI(uri)
                Glide.with(binding.pickerImage.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .override(500, 500)
                    .into(binding.pickerImage)
                currentUri = uri

                binding.ivIconAddPhoto.isVisible = false
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        observeViewModel()
        setupTextWatcher()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun setupTextWatcher() {

        binding.etNamePlaylist.doOnTextChanged { s, _, _, _ ->
            val text = s?.toString() ?: ""
            viewModel.editTextName(text)
        }

        binding.etDescriptionPlaylist.doOnTextChanged { s, _, _, _ ->
            val text = s?.toString() ?: ""
            viewModel.editTextDescription(text)
        }

    }

    private fun updateUi(state: AddMediaPlayerState) {

        with(binding) {
            btnCreatePlaylist.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    state.btnColor
                )
            )

            etNamePlaylist.background = ContextCompat.getDrawable(
                requireContext(),
                state.inputNameDrawable
            )

            etDescriptionPlaylist.background = ContextCompat.getDrawable(
                requireContext(),
                state.inputDescDrawable
            )

            btnCreatePlaylist.isEnabled = state.nameIsEnable
            etMiniNamePlaylist.isVisible = state.nameIsEnable
            etMiniDescriptionPlaylist.isVisible = state.descIsEnable
        }
    }

    private fun setClickListeners() {
        binding.toolbarPlaylist.setOnClickListener {
            checkIsEmptyDialogExit(viewModel.state.value)
        }

        binding.btnCreatePlaylist.setOnClickListener {
            val name = binding.etNamePlaylist.text.toString()
            val description = binding.etDescriptionPlaylist.text.toString()

            lifecycleScope.launch {
                binding.overlay.isVisible = true
                binding.progressBar.isVisible = true

                val result = viewModel.createPlayList(currentUri, name, description)

                binding.overlay.isVisible = false
                binding.progressBar.isVisible = false

                if (result){
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Ошибка при создании плейлиста", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    checkIsEmptyDialogExit(viewModel.state.value)
                }
            }
        )
    }

    private fun checkIsEmptyDialogExit(state: AddMediaPlayerState) {
        if (state.nameIsEnable || state.descIsEnable || currentUri != null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNeutralButton("Отмена") { dialog, which -> }
                .setPositiveButton("Завершить") { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    companion object {
    }
}