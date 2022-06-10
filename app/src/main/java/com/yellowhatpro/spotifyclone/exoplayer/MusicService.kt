package com.yellowhatpro.spotifyclone.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.yellowhatpro.spotifyclone.exoplayer.callbacks.MusicPlaybackPreparer
import com.yellowhatpro.spotifyclone.exoplayer.callbacks.MusicPlayerEventListener
import com.yellowhatpro.spotifyclone.exoplayer.callbacks.MusicPlayerNotificationListener
import com.yellowhatpro.spotifyclone.other.Constants.MEDIA_ROOT_ID
import com.yellowhatpro.spotifyclone.other.Constants.SERVICE_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory : DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer : ExoPlayer

    @Inject
    lateinit var localMusicSource: LocalMusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main+ serviceJob )

    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var mediaSessionConnector : MediaSessionConnector
    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    var isForegroundService = false
    private var isPlayerInitialized = false

    private var currentSong: MediaMetadataCompat? = null

    companion object {
        var currentSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            localMusicSource.fetchMediaData()

        }
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(this,SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(this, mediaSession.sessionToken, MusicPlayerNotificationListener(this)) {
            currentSongDuration = exoPlayer.duration
        }
        val musicPlaybackPreparer = MusicPlaybackPreparer(localMusicSource) {
            currentSong = it
            preparePlayer(
                localMusicSource.songs,
                it,
                true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)
        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID,null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                val resultsSent = localMusicSource.whenReady { isInitialized ->
                    if (isInitialized) {
                        result.sendResult(localMusicSource.asMediaItem())
                        if (!isPlayerInitialized && localMusicSource.songs.isNotEmpty()) {
                            preparePlayer(localMusicSource.songs, localMusicSource.songs[0], false)
                            isPlayerInitialized = true
                        }
                    } else {
                        result.sendResult(null)
                    }
                }
                if (!resultsSent) {
                    result.detach()
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }
    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        serviceScope.launch(Dispatchers.Main) {
            val currentSongIndex = if (currentSong == null) 0 else songs.indexOf(itemToPlay)
            exoPlayer.setMediaSource(localMusicSource.asMediaSource(dataSourceFactory))
            exoPlayer.prepare()
            exoPlayer.seekTo(currentSongIndex, 0L)
            exoPlayer.playWhenReady = playNow
        }
    }

    private inner class MusicQueueNavigator: TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return localMusicSource.songs[windowIndex].description
        }

    }
}