package com.yellowhatpro.spotifyclone.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.ViewModel
import com.yellowhatpro.spotifyclone.data.entities.Song
import com.yellowhatpro.spotifyclone.exoplayer.MusicServiceConnection
import com.yellowhatpro.spotifyclone.exoplayer.isPlayEnabled
import com.yellowhatpro.spotifyclone.exoplayer.isPlaying
import com.yellowhatpro.spotifyclone.exoplayer.isPrepared
import com.yellowhatpro.spotifyclone.other.Constants.MEDIA_ROOT_ID
import com.yellowhatpro.spotifyclone.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _mediaItems = MutableStateFlow<Resource<List<Song>>>(Resource.loading(null))
    val mediaItems = _mediaItems.asStateFlow()
    val isConnected = musicServiceConnection.isConnected
    private val currentlyPlayingSong = musicServiceConnection.currentPlayingSong
    private val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.value = (Resource.loading(null))
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Song(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri!!
                        )
                    }
                    _mediaItems.value = (Resource.success(items))
                }
            })
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaID == currentlyPlayingSong.value?.getString(
                METADATA_KEY_MEDIA_ID
            )
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaID, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {

            })
    }
}