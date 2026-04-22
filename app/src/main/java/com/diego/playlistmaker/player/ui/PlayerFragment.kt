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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayerBinding
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.player.models.PlayerState
import com.diego.playlistmaker.player.models.TrackInfo
import com.diego.playlistmaker.player.presenter.PlayListHorizontalAdapter
import com.diego.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val args: PlayerFragmentArgs by navArgs()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null
    private var isPlayList: Boolean = false

    private var lastPlayListList: List<PlayList>? = null

    private val playListAdapter: PlayListHorizontalAdapter by lazy {
        PlayListHorizontalAdapter(emptyList()) { playList -> onPlayListClicked(playList) }
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = args.track

        if (currentTrack != null) {
            currentTrack?.let { track ->
                viewModel.setTrack(track)
            }
            setupUI()
            setupObservers()
            setBottomSheet()

        } else {
            Toast.makeText(requireContext(),
                getString(R.string.eroor_load_track), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun onPlayListClicked(playList: PlayList) {
        viewModel.addTrackToPlayList(playList, currentTrack!!) { success ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val message = if (success) {
                "${getString(R.string.added_to_playlist)} ${playList.name}"
            } else {
                getString(R.string.track_is_added_to_playlist, playList.name)
            }

            binding.tvNamePlaylist.text = message
            viewLifecycleOwner.lifecycleScope.launch {
                binding.viewNamePlaylist.isVisible = true
                delay(2000)
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    binding.viewNamePlaylist.isVisible = false
                }
            }
        }
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


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        overlay.isVisible = true
        overlay.alpha = 0f
    }

    private fun setupPlaybackButton() {
        binding.btnPlayerPlay.onPlaybackStateChanged = { isPlaying ->
            if (isPlaying) {
                viewModel.play()
            } else {
                viewModel.pause()
            }
        }
    }

    private fun setupUI() {
        setupPlaybackButton()

        binding.toolbarPlayer.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPlayerLike.setOnClickListener {
            viewModel.likeTrack(currentTrack!!)
        }

        binding.btnAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnCreateNewPlaylist.setOnClickListener {

            val action = PlayerFragmentDirections.actionPlayerFragmentToAddMediaPlayerFragment()
            findNavController().navigate(action)
        }

        binding.recyclerBottomSheet.adapter = playListAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.screenState.collect {screenState ->
                    val trackInfo = screenState.trackInfo
                    val playerState = screenState.playerState
                    val position = screenState.currentPosition
                    val isLike = screenState.isLike
                    isPlayList = screenState.isPlayList

                    if (trackInfo != null) {
                        updateTrackUI(trackInfo)
                    }

                    // Обновляем плейлисты только если они изменились
                    if (screenState.playListList != lastPlayListList) {
                        lastPlayListList = screenState.playListList
                        playListAdapter.updatePlayList(screenState.playListList)
                    }

                    updatePlayerUI(playerState, isLike, isPlayList)
                    binding.trackCurrentTime.text = viewModel.getFormattedTime(position)
                }
            }
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
        isPlayList: Boolean
    ) {
        if (isLike) {
            binding.btnPlayerLike.setImageResource(R.drawable.ic_btn_like_is_like)
        } else {
            binding.btnPlayerLike.setImageResource(R.drawable.ic_btn_like)
        }

        if (isPlayList) {
            binding.btnAddToPlaylist.setImageResource(R.drawable.ic_btn_is_add_to_playlist)
        } else {
            binding.btnAddToPlaylist.setImageResource(R.drawable.ic_btn_add_to_playlist)
        }

        when (state) {
            PlayerState.DEFAULT -> {
                binding.btnPlayerPlay.isEnabled = false
                binding.trackCurrentTime.text = getString(R.string._00_00)

                binding.btnPlayerPlay.setPlaying(false)
            }

            PlayerState.PREPARING -> {
                binding.btnPlayerPlay.isEnabled = false
                binding.btnPlayerPlay.setPlaying(false)
            }

            PlayerState.PREPARED -> {
                binding.btnPlayerPlay.isEnabled = true
                binding.btnPlayerPlay.setPlaying(false)
            }

            PlayerState.PLAYING -> {
                binding.btnPlayerPlay.isEnabled = true
                binding.btnPlayerPlay.setPlaying(true)
            }

            PlayerState.PAUSED -> {
                binding.btnPlayerPlay.isEnabled = true
                binding.btnPlayerPlay.setPlaying(false)
            }

            PlayerState.ERROR -> {
                binding.btnPlayerPlay.isEnabled = false
                binding.btnPlayerPlay.setPlaying(false)
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