package com.yellowhatpro.yellowmusic.utils

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.yellowhatpro.yellowmusic.data.entities.Song

object Constants {
    const val SONG_COLLECTION = "songs"
    const val SERVICE_TAG = "MusicService"
    const val NOTIFICATION_CHANNEL_ID = "Music"
    const val NOTIFICATION_ID = 1
    const val MEDIA_ROOT_ID = "root_id"
    val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()

    val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

}