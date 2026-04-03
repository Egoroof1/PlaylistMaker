package com.diego.playlistmaker.media.ui.fragments

import android.os.Bundle
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
import com.diego.playlistmaker.R
import com.diego.playlistmaker.databinding.FragmentPlayListBinding
import com.diego.playlistmaker.media.ui.state.PlayListState
import com.diego.playlistmaker.media.ui.view_model.PlayListViewModel
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.search.presentation.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayListFragment : Fragment() {

    private val args: PlayListFragmentArgs by navArgs()
    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayListViewModel by viewModel()
    private var isFirstCreation: Boolean = false
    private var currentTrackForDeletion: Track? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val trackAdapter: TrackAdapter by lazy {
        TrackAdapter(
            tracks = emptyList(),
            onTrackClick = { track ->
                onTrackClicked(track)
            },
            onTrackLongClicked = { track ->
                onTrackLongClicked(track)
            }
        )
    }

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
        setOnClickListener()

        binding.recyclerBottomSheet.adapter = trackAdapter
    }

    private fun onTrackClicked(track: Track) {
        val action = PlayListFragmentDirections.actionPlayListListFragmentToPlayerFragment(track)
        findNavController().navigate(action)
    }

    private fun onTrackLongClicked(track: Track) {
        currentTrackForDeletion = track
        showDeleteTrackDialog()
    }

    private fun showDeleteTrackDialog() {
        val track = currentTrackForDeletion ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dyw_delete_playlist))
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { dialog, which -> }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.deleteTrack(track, currentPlayListId)
            }
            .show()
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivSharing.setOnClickListener {
            sharePlaylist()
        }

        binding.ivMenu.setOnClickListener {
            binding.recyclerBottomSheet.isVisible = false
            bottomSheetBehavior.apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                isHideable = true
                peekHeight = peekHeight + 200
            }
        }

        binding.btnShare.setOnClickListener {
            sharePlaylist()
        }

        binding.btnDeletePlaylist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_playlist))
                .setMessage(getString(R.string.dyw_delete_playlist, "<<${viewModel.state.value.playList?.name}>>"))
                .setNegativeButton(getString(R.string.no)) { dialog, which -> }
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deletePlaylist(viewModel.state.value.playList?.id ?: -1)
                    findNavController().popBackStack()
                }
                .show()
        }

        binding.btnEditInfo.setOnClickListener {
            val action = PlayListFragmentDirections.actionPlayListListFragmentToEditPlayListFragment(args.playListId)
            findNavController().navigate(action)
        }
    }

    private fun sharePlaylist(){
        if (viewModel.state.value.trackList.isEmpty()){
            Toast.makeText(
                requireContext(),
                getString(R.string.tracks_list_is_empty_share_error),
                Toast.LENGTH_SHORT).show()
        } else {
            viewModel.sharePlayList(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPlayListById(currentPlayListId)
            viewModel.getTracksFromPlayList(currentPlayListId)

            viewModel.state.collect { state ->
                if (_binding != null) {

                    updateUi(state)
                    trackAdapter.updateList(state.trackList)
                }
            }
        }
    }

    private fun setBottomSheet() {
        val bottomSheetContainer = binding.playlistBottomSheet
        val overlay = binding.overlay

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
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
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        binding.recyclerBottomSheet.isVisible = true
                        bottomSheetBehavior.peekHeight = bottomSheetBehavior.peekHeight - 200
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

    private fun updateUi(state: PlayListState) {
        if (_binding == null) return

        val playList = state.playList

        if (playList == null) return

        with(binding) {
            Glide.with(ivCaverPlaylist.context)
                .load(playList.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivCaverPlaylist)

            tvNamePlaylist.text = playList.name
            tvYearPlaylist.text = playList.description
            tvTotalTimePlaylist.text =
                getString(R.string.minuts, playList.totalTimeMillis / 1000 / 60)
            tvQuantityTracksPlaylist.text = getTracksCountText(playList.quantityTracks)
        }

        with(binding.includeLayoutPlaylist){
            Glide.with(image.context)
                .load(playList.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(image)

            etNamePlaylist.text = playList.name
            tvItemPlaylistQuantityTracks.text = getTracksCountText(playList.quantityTracks)
        }
    }

    private fun getTracksCountText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> getString(R.string.count_tracks, count)
            count % 10 in 2..4 && (count % 100 !in 12..14) -> getString(R.string.count_tracks, count)
            else -> getString(R.string.count_tracks, count)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}