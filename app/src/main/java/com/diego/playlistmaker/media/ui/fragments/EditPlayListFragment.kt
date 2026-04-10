package com.diego.playlistmaker.media.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
import com.diego.playlistmaker.databinding.FragmentAddMediaPlayerBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import com.diego.playlistmaker.media.ui.view_model.EditPlayListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlayListFragment : AddMediaPlayerFragment() {
    private val args: EditPlayListFragmentArgs by navArgs()
    private val viewModel: EditPlayListViewModel by viewModel()

    private var oldPlayList: PlayList? = null

    override val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.pickerImage.setImageURI(uri)
                Glide.with(binding.pickerImage.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
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
        binding = FragmentAddMediaPlayerBinding.inflate(inflater, container, false)
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

    override fun setupTextWatcher() {

        binding.etNamePlaylist.doOnTextChanged { s, _, _, _ ->
            val text = binding.etNamePlaylist.text.toString()
            viewModel.editTextName(text)
        }

        binding.etDescriptionPlaylist.doOnTextChanged { s, _, _, _ ->
            val text = binding.etDescriptionPlaylist.text.toString()
            viewModel.editTextDescription(text)
        }

    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (oldPlayList == null) {
                    oldPlayList = state.playList
                    setFirstUI()
                }

                updateUi(state)
            }
        }
    }

    override fun updateUi(state: AddMediaPlayerState) {
        with(binding) {
            btnCreatePlaylist.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    state.btnColor
                )
            )
            btnCreatePlaylist.isEnabled = state.isBtnEnable

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

        binding.toolbarPlaylist.title = getString(R.string.edit)
        binding.btnCreatePlaylist.text = getString(R.string.save)

        with(binding) {
            Glide.with(pickerImage.context)
                .load(oldPlayList?.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
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

            btnCreatePlaylist.setOnClickListener {
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

    override fun checkIsEmptyDialogExit(state: AddMediaPlayerState) {
        if (state.nameIsEnable || state.descIsEnable || currentUri != null) {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
                .setTitle(getString(R.string.finish_creating_the_playlist))
                .setMessage(getString(R.string.all_unsaved_data_will_be_lost))
                .setNeutralButton(getString(R.string.cancellation)) { dialog, which -> }
                .setPositiveButton(getString(R.string.complete)) { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }
}