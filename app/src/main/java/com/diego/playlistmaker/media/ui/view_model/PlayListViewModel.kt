package com.diego.playlistmaker.media.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.mapper.formatDuration
import com.diego.playlistmaker.media.domain.mapper.getTracksCountText
import com.diego.playlistmaker.media.domain.models.PlayList
import com.diego.playlistmaker.media.domain.use_case.ImageStorageInteractor
import com.diego.playlistmaker.media.domain.use_case.PlayListInteractor
import com.diego.playlistmaker.media.domain.use_case.TrackInPlayListInteractor
import com.diego.playlistmaker.media.ui.state.PlayListState
import com.diego.playlistmaker.search.domain.models.Track
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayListViewModel(
    private val playListInteractor: PlayListInteractor,
    private val trackInPlayList: TrackInPlayListInteractor,
    private val sharingInteractor: SharingInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(PlayListState())
    var state: StateFlow<PlayListState> = _state



    suspend fun getPlayListById(playListId: Int): PlayList?{
        val playList: PlayList? = withContext(Dispatchers.IO) {
            playListInteractor.getPlayListById(playListId)
        }
        updateState { it.copy(playList = playList) }

        return playList
    }

    fun deletePlaylist(playListId: Int){
        if (playListId < 0) return
        viewModelScope.launch(Dispatchers.IO) {
            trackInPlayList.deleteAllTrackFromPlaylist(playListId)
            playListInteractor.deletePlayListById(playListId)
            imageStorageInteractor.deleteImage(state.value.playList?.coverImagePath ?: "")
        }

    }

    fun sharePlayList(context: Context) {
        val tracks = state.value.trackList
        val playList = state.value.playList

        val resources = context.resources

        val res = StringBuilder()
        res.append("${playList?.name}\n")

        val description = playList?.description ?: ""
        if (description.isNotEmpty()){
            res.append("$description\n")
        }

        res.append(resources.getTracksCountText(tracks.size))

        var num = 0

        for (i in tracks){
            res.append("\n${++num}. ${i.artistName} - ${i.trackName} (${i.trackTimeMillis.formatDuration()})")
        }

        sharingInteractor.shareApp(res.toString())
    }

    fun getTracksFromPlayList(playListId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            trackInPlayList.getAllTracksInPlayListByIdPlaylist(playListId).collect { tracks ->
                updateState {
                    it.copy(trackList = tracks)
                }
            }
        }
    }

    fun deleteTrack(track: Track, playListId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                trackInPlayList.deleteTrackForPlayListById(track.trackId, playListId)

                playListInteractor.decrementTracksCount(playListId)
                playListInteractor.addTotalTimeMillis(playListId, -track.trackTimeMillis)

                val updatedPlayList = playListInteractor.getPlayListById(playListId)
                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(playList = updatedPlayList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateState(updater: (PlayListState) -> PlayListState){
        val currentState = _state.value
        _state.value = updater(currentState)
    }
}