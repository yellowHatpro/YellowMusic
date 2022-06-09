package com.yellowhatpro.spotifyclone.exoplayer

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yellowhatpro.spotifyclone.utils.Event
import com.yellowhatpro.spotifyclone.utils.Resource

class MusicServiceConnection(
    context: Context,
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentPlayingSong: LiveData<MediaMetadataCompat?> = _currentPlayingSong

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    //To skip, pause, resume etc in player
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    //subscribe and unsubscribe will be called from VM to subs and unsub from a mediaID to get access mediaItems from local
    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }
    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentPlayingSong.postValue(metadata)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        //Callbacks to listen the following events
        override fun onConnected() {
             mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken ).apply {
                 registerCallback(MediaControllerCallback())
             }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error("Connection was suspended", false)))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error(
                "Coudn't connect to media browser", false)))
        }
    }
}