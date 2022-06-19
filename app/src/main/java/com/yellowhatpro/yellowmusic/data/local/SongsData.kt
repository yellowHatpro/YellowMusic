package com.yellowhatpro.yellowmusic.data.local

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.yellowhatpro.yellowmusic.data.entities.Song

class SongsData (private val context: Context) {

    private val songs = mutableListOf<Song>()
    fun fetchSongs(): List<Song> {
        val collection =
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            }else{
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val songProjection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.SIZE
        )
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        val isMusic = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val songQuery = context.contentResolver.query(
            collection,
            songProjection,
            isMusic,
            null,
            sortOrder
        )
        songQuery?.use { cursor ->
            val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val name = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            while (cursor.moveToNext()){
                val song_id = cursor.getLong(id).toString()
                val song_name = cursor.getString(name)
                val artist = cursor.getString(artist)
                val contentUri = ContentUris.withAppendedId(
                    collection,
                    song_id.toLong()
                ).toString()
                songs+= Song(song_id,song_name,artist,contentUri)
            }
        }
        return songs
    }
}