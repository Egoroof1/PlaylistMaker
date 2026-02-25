package com.diego.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diego.playlistmaker.media.domain.use_case.FavoriteRepositoryUseCase
import com.diego.playlistmaker.media.ui.state.TracksState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TracksFragmentViewModel(
    private val repository: FavoriteRepositoryUseCase
) : ViewModel() {

    private val _tracksState = MutableStateFlow(TracksState())
    val tracksState: StateFlow<TracksState> = _tracksState

    init {
        observeFavoriteTracks()
    }

    private fun observeFavoriteTracks() {

        viewModelScope.launch {
            repository.favoriteTracks().collect { tracks ->
                updateState { currentState ->
                    currentState.copy(
                        tracksList = tracks
                    )
                }
            }

            Log.d("TAG", "observeFavoriteTracks: ${ repository.isFavorite(123) }")
        }
    }

    private fun updateState(updater: (TracksState) -> TracksState) {
        val currentState = _tracksState.value
        _tracksState.value = updater(currentState)
    }


//    val tracksList = listOf(
//        Track(
//            trackId = 264135842,
//            trackName = "Murmaider",
//            artistName = "Metalocalypse: Dethklok",
//            collectionName = "The Dethalbum",
//            releaseDate = "2007-06-25T12:00:00Z",
//            primaryGenreName = "Rock",
//            country = "USA",
//            trackTimeMillis = 204360,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music/ef/87/83/mzi.jtfcshqw.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/f7/66/05/f76605c7-a57c-f2c2-b506-caa9e3a59a40/mzaf_6581404682928693795.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1628314968,
//            trackName = "OXI",
//            artistName = "LILA",
//            collectionName = "XITANA - EP",
//            releaseDate = "2022-06-16T12:00:00Z",
//            primaryGenreName = "Pop",
//            country = "USA",
//            trackTimeMillis = 168511,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music122/v4/d0/5e/e8/d05ee83b-c369-f065-c279-f7ebfa255078/196925198660.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/c6/21/2b/c6212b1c-a262-f557-1298-7ff0aadb1e73/mzaf_2159996358477282705.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1364709436,
//            trackName = "One Kiss",
//            artistName = "Calvin Harris, Dua Lipa",
//            collectionName = "One Kiss - Single",
//            releaseDate = "2018-04-06T07:00:00Z",
//            primaryGenreName = "Dance",
//            country = "USA",
//            trackTimeMillis = 214847,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/91/2a/7b/912a7bdb-c2f6-b887-9392-49728fece0df/886447044360.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/7b/51/0a/7b510a93-10b2-530f-71fb-2462f7606e6d/mzaf_907293082198573368.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 264136716,
//            trackName = "Briefcase Full of Guts",
//            artistName = "Metalocalypse: Dethklok",
//            collectionName = "The Dethalbum",
//            releaseDate = "2007-06-25T12:00:00Z",
//            primaryGenreName = "Rock",
//            country = "USA",
//            trackTimeMillis = 163840,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music/ef/87/83/mzi.jtfcshqw.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/61/f5/a4/61f5a482-7cd1-b0ca-37b7-12afe7eeb66f/mzaf_11522138742347181684.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1679480513,
//            trackName = "Awaken",
//            artistName = "Metalocalypse: Dethklok",
//            collectionName = "The Dethalbum (Expanded Edition)",
//            releaseDate = "2007-06-25T12:00:00Z",
//            primaryGenreName = "Metal",
//            country = "USA",
//            trackTimeMillis = 217400,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music126/v4/e2/5c/3c/e25c3cfb-bd9f-3a12-4b35-e8c52e34442f/794043215025.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview126/v4/77/83/05/77830568-2247-8baa-6e09-7a2bc54705a4/mzaf_10359917777535544151.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1691421683,
//            trackName = "Deadlock",
//            artistName = "Bossfight & Hayve",
//            collectionName = "Deadlock - Single",
//            releaseDate = "2023-06-26T12:00:00Z",
//            primaryGenreName = "Dubstep",
//            country = "USA",
//            trackTimeMillis = 207000,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/13/8b/5b/138b5bee-5d98-0c3b-cbbc-427b894e399b/742779548559.png/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview116/v4/a5/c8/51/a5c851d2-b6b1-9594-500f-52ea5b25fe21/mzaf_9142133844881716920.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1770236402,
//            trackName = "GG",
//            artistName = "ReoNa",
//            collectionName = "GG - Single",
//            releaseDate = "2025-09-24T12:00:00Z",
//            primaryGenreName = "Anime",
//            country = "USA",
//            trackTimeMillis = 196347,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/4f/08/67/4f0867b2-3d82-9b22-ceb4-d41774823cba/4547366710786.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/b5/3f/08/b53f08c1-586b-2a16-1a3e-9a712a2735f6/mzaf_5497051592998855303.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1873549957,
//            trackName = "Bread",
//            artistName = "ggg.",
//            collectionName = "Baguette.",
//            releaseDate = "2026-01-30T12:00:00Z",
//            primaryGenreName = "Jazz",
//            country = "USA",
//            trackTimeMillis = 153902,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/58/62/f9/5862f94c-fc24-c26c-9230-4de5180569ab/859730328164_cover.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/b3/a2/7a/b3a27a32-e713-4908-2ffa-164146242e5f/mzaf_14594904987637014466.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1661374609,
//            trackName = "Gg",
//            artistName = "Sin Boy & Rina",
//            collectionName = "Gg - Single",
//            releaseDate = "2020-05-16T12:00:00Z",
//            primaryGenreName = "Pop",
//            country = "USA",
//            trackTimeMillis = 185771,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music123/v4/e7/54/9d/e7549df6-9888-fa17-eb3d-391a4013d211/48533.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview123/v4/8b/31/d6/8b31d6b2-fdbe-5d88-ded0-0e10b67e81ad/mzaf_15121408217768162321.plus.aac.p.m4a"
//        ),
//        Track(
//            trackId = 1440762729,
//            trackName = "Smooth Criminal",
//            artistName = "Alien Ant Farm",
//            collectionName = "ANThology",
//            releaseDate = "2001-03-06T12:00:00Z",
//            primaryGenreName = "Hard Rock",
//            country = "USA",
//            trackTimeMillis = 209267,
//            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/31/ab/5c/31ab5cde-ce71-8bed-618b-f32629f765c9/06UMGIM18711.rgb.jpg/100x100bb.jpg",
//            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/f0/1e/05/f01e0513-ee09-10ee-b6ab-fc1386330acd/mzaf_4400656116272011484.plus.aac.p.m4a"
//        )
//    )
}