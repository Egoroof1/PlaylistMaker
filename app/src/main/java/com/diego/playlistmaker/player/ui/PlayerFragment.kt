package com.diego.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayerBinding
import com.diego.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    // Получаем аргументы через Safe Args
    private val args: PlayerFragmentArgs by navArgs()

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()

    private var currentTrack: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = args.track

        if (currentTrack != null) {
            setupUI()
            setupObservers()
            viewModel.setTrack(currentTrack!!)
            viewModel.preparePlayer(currentTrack!!.previewUrl)
        } else {
            Toast.makeText(requireContext(), "Ошибка загрузки трека", Toast.LENGTH_SHORT).show()
            // Возвращаемся назад
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        // Настройка toolbar - возврат назад
        binding.toolbarPlayer.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка воспроизведения/паузы
        binding.btnPlayerPlay.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }

    private fun setupObservers() {
        // Наблюдаем за информацией о треке
        viewModel.trackInfo.observe(viewLifecycleOwner) { trackInfo ->
            if (trackInfo != null) {
                updateTrackUI(trackInfo)
            }
        }

        // Наблюдаем за состоянием плеера
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            updatePlayerUI(state)
        }

        // Наблюдаем за текущей позицией трека
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            binding.trackCurrentTime.text = viewModel.getFormattedTime(position)
        }
    }

    private fun updateTrackUI(trackInfo: PlayerViewModel.TrackInfo) {
        // Загрузка изображения
        Glide.with(binding.image)
            .load(trackInfo.artworkUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(trackInfo.originalArtworkUrl)
            .transform(RoundedCorners(dpToPx(8f)))
            .into(binding.image)

        // Установка текстовых полей
        binding.apply {
            playerTrackName.text = trackInfo.trackName
            playerArtistName.text = trackInfo.artistName
            trackTimeMillis.text = viewModel.getFormattedTime(trackInfo.trackTimeMillis.toInt())
            trackAlbumName.text = trackInfo.albumName
            playerTrackYear.text = trackInfo.releaseYear
            playerTrackGenre.text = trackInfo.genre
            playerTrackCountry.text = trackInfo.country
        }
    }

    private fun updatePlayerUI(state: PlayerViewModel.PlayerState) {
        when (state) {
            PlayerViewModel.PlayerState.DEFAULT -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                binding.trackCurrentTime.text = getString(R.string._00_00)
            }

            PlayerViewModel.PlayerState.PREPARING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
            }

            PlayerViewModel.PlayerState.PREPARED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.PLAYING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_pause)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.PAUSED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerViewModel.PlayerState.ERROR -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                Toast.makeText(requireContext(), getString(R.string.replication_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }
}