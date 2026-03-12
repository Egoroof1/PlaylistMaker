package com.diego.playlistmaker.media.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.diego.playlistmaker.databinding.FragmentAddMediaPlayerBinding
import com.diego.playlistmaker.media.ui.state.AddMediaPlayerState
import com.diego.playlistmaker.media.ui.view_model.AddMediaPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddMediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentAddMediaPlayerBinding

    private val viewModel: AddMediaPlayerViewModel by viewModel()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.pickerImage.setImageURI(uri)
                saveImageToPrivateStorage(uri)
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

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
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

        with(binding){
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

    private fun setClickListeners(){
        binding.toolbarPlaylist.setOnClickListener {
            checkIsEmptyDialogExit(viewModel.state.value)
        }

        binding.btnCreatePlaylist.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.etNamePlaylist.text} создан",
                Toast.LENGTH_SHORT).show()

            findNavController().popBackStack()
        }

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            // берём путь к файлу, затем берём сам файл, ставим его в картинку
//            val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
//            val file = File(filePath, "first_cover.jpg")
//            binding.pickerImage.setImageURI(file.toUri())
        }
    }

    private fun checkIsEmptyDialogExit(state: AddMediaPlayerState) {
        if (state.nameIsEnable || state.descIsEnable || state.image.isNotEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNeutralButton("Отмена"){ dialog, which -> }
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