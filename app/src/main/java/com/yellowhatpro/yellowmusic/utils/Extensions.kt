package com.yellowhatpro.yellowmusic.utils

import android.support.v4.media.MediaMetadataCompat
import com.yellowhatpro.yellowmusic.data.entities.Song

object Extensions {
    inline val MediaMetadataCompat?.toSong
        get() = this?.description?.let {
            Song(
                mediaID = it.mediaId.toString(),
                title = it.title.toString(),
                artist = it.subtitle.toString(),
                uri = it.mediaUri.toString()
            )
        } ?: Song()

    inline val Song?.toMediaMetadataCompat
        get() = this?.let { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.mediaID)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.uri.toString())
                .build()
        }
}