package com.diego.playlistmaker.media.ui.fragments

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentEditPlayListBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.ui.state.EditPLState
import com.diego.playlistmaker.media.ui.view_model.EditPlayListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlayListFragment : Fragment() {

    private lateinit var binding: FragmentEditPlayListBinding
    private val args: EditPlayListFragmentArgs by navArgs()
    private val viewModel: EditPlayListViewModel by viewModel()

    private var oldPlayList: PlayList? = null

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
                viewModel.setNewImage()
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
        binding = FragmentEditPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setPlayList(args.playListId)

        observeViewModel()
        setFirstUI()
        setupClickListeners()
        setupTextWatcher()

    }

    private fun setupTextWatcher() {

        binding.etNamePlaylist.doOnTextChanged { s, _, _, _ ->
            val text = binding.etNamePlaylist.text.toString()
            viewModel.editTextName(text)
        }

        binding.etDescriptionPlaylist.doOnTextChanged { s, _, _, _ ->
            val text = binding.etDescriptionPlaylist.text.toString()
            viewModel.editTextDescription(text)
        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (oldPlayList == null) {
                    oldPlayList = state.playList
                    setFirstUI()
                }

                updateUI(state)
            }
        }
    }

    private fun updateUI(state: EditPLState){
        with(binding) {
            btnSavePlaylist.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    state.btnColor
                )
            )
            btnSavePlaylist.isEnabled = state.isBtnEnable

            etNamePlaylist.background = ContextCompat.getDrawable(
                requireContext(),
                state.inputNameDrawable
            )

            etDescriptionPlaylist.background = ContextCompat.getDrawable(
                requireContext(),
                state.inputDescDrawable
            )

            etMiniNamePlaylist.isVisible = state.nameIsEnable
            etMiniDescriptionPlaylist.isVisible = state.descIsEnable
        }
    }

    private fun setFirstUI() {
        if (oldPlayList == null) return
        with(binding) {
            Glide.with(pickerImage.context)
                .load(oldPlayList?.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .override(500, 500)
                .into(pickerImage)

            etNamePlaylist.setText(oldPlayList?.name)
            etDescriptionPlaylist.setText(oldPlayList?.description)
        }
    }

    private fun setupClickListeners() {
        with(binding) {

            toolbarPlaylist.setOnClickListener {
                checkIsEmptyDialogExit(viewModel.state.value)
            }

            btnSavePlaylist.setOnClickListener {
                viewModel.updatePlayList(
                    id = args.playListId,
                    etNamePlaylist.text.toString(),
                    etDescriptionPlaylist.text.toString(),
                    currentUri ?: "".toUri()
                )
                findNavController().popBackStack()
            }

            pickerImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
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

    private fun checkIsEmptyDialogExit(state: EditPLState) {
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
}