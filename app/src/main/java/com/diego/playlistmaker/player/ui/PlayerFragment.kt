package com.diego.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayerBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.player.models.PlayerState
import com.diego.playlistmaker.player.models.TrackInfo
import com.diego.playlistmaker.player.presenter.PlayListAdapter
import com.diego.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val args: PlayerFragmentArgs by navArgs()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null
    private var isPlayList: Boolean = false
    private var isFirstCreation: Boolean = false

    private val playListAdapter: PlayListAdapter by lazy {
        PlayListAdapter(emptyList()) { playList -> onPlayListClicked(playList) }
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
            viewModel.setTrack(currentTrack!!)
            setupUI()
            setupObservers()
            setBottomSheet()

        } else {
            Toast.makeText(requireContext(), "Ошибка загрузки трека", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun onPlayListClicked(playList: PlayList) {


        viewModel.addTrackToPlayList(playList.id, currentTrack!!)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Toast.makeText(requireContext(), "Трек добавлен в ${playList.name}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setBottomSheet() {
        val bottomSheetContainer = binding.playlistBottomSheet
        val overlay = binding.overlay

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

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
                val slideAdapter = when {
                    slideOffset <= 0f -> (slideOffset + 1f) / 2f
                    else -> (slideOffset / 2f) + 0.5f
                }
                overlay.alpha = slideAdapter
            }
        })


        if (!isFirstCreation) {
            // Первый вход - скрываем bottom sheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            isFirstCreation = true
            overlay.isVisible = true
            overlay.alpha = 0f
        } else {
            // При возвращении - синхронизируем overlay с текущим состоянием behavior
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    overlay.isVisible = false
                }
                else -> {
                    overlay.isVisible = true
                    overlay.alpha = 0.5f
                }
            }
        }
    }

    private fun setupUI() {
        binding.toolbarPlayer.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPlayerPlay.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.btnPlayerLike.setOnClickListener {
            viewModel.likeTrack(currentTrack!!)
        }

        binding.btnAddToPlaylist.setOnClickListener {
            if (!isPlayList) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            binding.btnAddToPlaylist.setImageResource(R.drawable.ic_btn_is_add_to_playlist)

//            try {
//                val action = PlayerFragmentDirections.actionPlayerFragmentToPlayListFragment()
//                findNavController().navigate(action)
//            } catch (e: Exception){
//                e.printStackTrace()
//            }

        }

        binding.btnCreateNewPlaylist.setOnClickListener {

            val action = PlayerFragmentDirections.actionPlayerFragmentToAddMediaPlayerFragment()
            findNavController().navigate(action)
        }

        binding.recyclerBottomSheet.adapter = playListAdapter
    }

    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenState.observe(viewLifecycleOwner) { screenState ->
                val trackInfo = screenState.trackInfo
                val playerState = screenState.playerState
                val position = screenState.currentPosition
                val isLike = screenState.isLike
                isPlayList = screenState.isPlayList
                val playListName = screenState.playListName

                if (trackInfo != null) {
                    updateTrackUI(trackInfo)
                }

                updatePlayerUI(playerState, isLike, isPlayList, playListName)
                binding.trackCurrentTime.text = viewModel.getFormattedTime(position)
            }
        }

        viewModel.screenState.observe(viewLifecycleOwner) { stats ->
            // Обновляем адаптер
            playListAdapter.playLists = stats.playListList
            playListAdapter.notifyDataSetChanged()
        }
    }

    private fun updateTrackUI(trackInfo: TrackInfo) {
        Glide.with(binding.image)
            .load(trackInfo.artworkUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(trackInfo.originalArtworkUrl)
            .transform(RoundedCorners(8f.dpToPx()))
            .into(binding.image)

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

    private fun updatePlayerUI(
        state: PlayerState,
        isLike: Boolean,
        isPlayList: Boolean,
        playListName: String
    ) {
        if (isLike) {
            binding.btnPlayerLike.setImageResource(R.drawable.ic_btn_like_is_like)
        } else {
            binding.btnPlayerLike.setImageResource(R.drawable.ic_btn_like)
        }

        if (isPlayList) {
            binding.btnAddToPlaylist.setImageResource(R.drawable.ic_btn_is_add_to_playlist)

            binding.tvNamePlaylist.text = "${getString(R.string.added_to_playlist)} $playListName"
            binding.viewNamePlaylist.isVisible = true

            binding.emptyView.isVisible = true
        } else {
            binding.btnAddToPlaylist.setImageResource(R.drawable.ic_btn_add_to_playlist)
            binding.viewNamePlaylist.isVisible = false

            binding.emptyView.isVisible = false
        }

        when (state) {
            PlayerState.DEFAULT -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                binding.trackCurrentTime.text = getString(R.string._00_00)
            }

            PlayerState.PREPARING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
            }

            PlayerState.PREPARED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerState.PLAYING -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_pause)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerState.PAUSED -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = true
            }

            PlayerState.ERROR -> {
                binding.btnPlayerPlay.setImageResource(R.drawable.ic_btn_play)
                binding.btnPlayerPlay.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.replication_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun Float.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
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